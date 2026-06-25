# Stonks — Codebase Tour

A structured reading order that follows the data flow from the outside in, then back out to the UI.

---

## Mental Model

```
MainActivity
  └── NavigationBar (Markets | News)
        ├── CompanyListingsViewModel
        │     ├── GetCompanyListingsUseCase  →  StockRepository
        │     │                                   ├── FinnhubApi   (remote fetch)
        │     │                                   └── StockDao     (cache read/write)
        │     ├── GetStockQuoteUseCase       →  StockRepository → FinnhubApi
        │     └── ToggleFavoriteUseCase      →  StockRepository → StockDao
        │
        ├── CompanyInfoViewModel
        │     └── GetCompanyDetailUseCase
        │           ├── GetCompanyInfoUseCase  →  StockRepository → FinnhubApi
        │           └── GetStockQuoteUseCase   →  StockRepository → FinnhubApi
        │
        └── NewsViewModel
              └── GetMarketNewsUseCase  →  StockRepository → FinnhubApi
```

The ViewModels never touch Retrofit or Room directly — they only talk to use cases, which only talk to interfaces, which are injected by Hilt with their real implementations. That's the Clean Architecture boundary in practice.

The listings screen is the most complex because it coordinates three use cases: `GetCompanyListingsUseCase` drives the main list (cache-then-network with a debounced search), `ToggleFavoriteUseCase` writes the watchlist flag, and `GetStockQuoteUseCase` is called concurrently for every favorited symbol to show live price changes on the Watchlist tab.

---

## Layer 1 — Domain

> The "what". Pure Kotlin — no Android or framework dependencies. Everything else depends on this layer; it depends on nothing.

| #  | File                                       | Purpose                                                                                         |
|----|--------------------------------------------|-------------------------------------------------------------------------------------------------|
| 1  | `util/Resource.kt`                         | Generic `Success / Loading / Error` sealed class. Repositories return it; ViewModels map it to UI state fields. Read this first — it's referenced everywhere. |
| 2  | `domain/model/CompanyListingDomainModel.kt`| A single row in the listings list. Holds `name`, `symbol`, `exchange`, and `isFavorite`. The watchlist is just a filtered view of this same type. |
| 3  | `domain/model/StockQuoteDomainModel.kt`    | Real-time price snapshot (`current`, `high`, `low`, `open`, `previousClose`). Price change and % change are computed from `current − previousClose` at the call site — no derived fields stored here. |
| 4  | `domain/model/CompanyInfoDomainModel.kt`   | Company profile (`name`, `country`, `industry`, `logoUrl`). Separate from the listing model because it comes from a different endpoint. |
| 5  | `domain/model/CompanyDetailDomainModel.kt` | Internal aggregation type used only by `GetCompanyDetailUseCase`. Bundles `CompanyInfoDomainModel` with an optional `StockQuoteDomainModel` so the detail Flow can emit partial state before the quote arrives. |
| 6  | `domain/model/NewsArticleDomainModel.kt`   | A market news article: headline, summary, source, image URL, article URL, Unix timestamp, category. |
| 7  | `domain/repository/StockRepository.kt`     | The single domain contract for all I/O. Read this to understand the full capability surface before touching any data-layer code. |
| 8  | `domain/usecase/GetCompanyListingsUseCase.kt` | Thin delegation to `StockRepository.getCompanyListings`. Returns a `Flow` so the VM can react to cache emissions before the network response arrives. |
| 9  | `domain/usecase/GetStockQuoteUseCase.kt`   | Single-shot quote fetch. Used in two places: the detail screen and the watchlist quote loader. |
| 10 | `domain/usecase/GetCompanyInfoUseCase.kt`  | Single-shot company profile fetch.                                                              |
| 11 | `domain/usecase/GetCompanyDetailUseCase.kt`| The most interesting use case. Orchestrates `GetCompanyInfoUseCase` and `GetStockQuoteUseCase` into a single `Flow`, emitting an intermediate `Success(info, null)` immediately, then waiting 1100 ms (Finnhub free-tier rate limit) before fetching and emitting the quote. Read alongside `CompanyInfoViewModel` to see how the two-phase emission maps to `isQuoteLoading`. |
| 12 | `domain/usecase/ToggleFavoriteUseCase.kt`  | Writes the new `isFavorite` flag for a symbol. Used by the listings VM on star tap.            |
| 13 | `domain/usecase/GetMarketNewsUseCase.kt`   | Single-shot news fetch.                                                                         |

---

## Layer 2 — Data

> The "how". Fulfills the contracts defined in the domain layer.

| #  | File                                       | Purpose                                                                                                                                   |
|----|--------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------|
| 14 | `data/remote/dto/FinnhubSymbolDto.kt`      | DTO for one row from the symbols listing endpoint. Three fields: `symbol`, `description` (display name), `mic` (exchange).                |
| 15 | `data/remote/dto/FinnhubProfileDto.kt`     | DTO for the company profile endpoint. `@param:Json` used on all fields to avoid the Kotlin annotation-target ambiguity warning.           |
| 16 | `data/remote/dto/FinnhubQuoteDto.kt`       | DTO for the real-time quote endpoint. Field names are single letters (`c`, `h`, `l`, `o`, `pc`) — the mappers give them readable names.   |
| 17 | `data/remote/dto/FinnhubNewsDto.kt`        | DTO for a single news article from the market news endpoint.                                                                              |
| 18 | `data/remote/api/FinnhubApi.kt`            | Retrofit interface with four endpoints. All inject the API key from `BuildConfig.FINNHUB_API_KEY` as a default parameter — no key strings in call sites. |
| 19 | `data/local/entity/CompanyListingEntity.kt`| Room entity. Notice `isFavorite: Boolean = false` — this column was added in migration 2, which is why `StockDatabase` carries `MIGRATION_1_2`. |
| 20 | `data/local/dao/StockDao.kt`               | All Room operations for listings: bulk insert/replace, full clear, search, `toggleFavorite` (single UPDATE), and `getFavoritedSymbols`/`restoreFavorites` — the pair that preserves the watchlist across a wipe-and-reload refresh. |
| 21 | `data/local/StockDatabase.kt`              | Room database class. Hosts `MIGRATION_1_2` as a companion object constant so it can be referenced from `AppModule` without importing an unrelated class. |
| 22 | `data/mapper/CompanyMapper.kt`             | The most populated mapper file. Read in this order: entity → domain (for cache reads), DTO → domain (for remote results), domain → entity (for cache writes). Also maps profile and quote DTOs. |
| 23 | `data/mapper/NewsMapper.kt`                | Maps `FinnhubNewsDto → NewsArticleDomainModel`. Returns null for articles missing a headline or URL, so `mapNotNull` in the repository silently drops malformed rows. |
| 24 | `data/repository/StockRepositoryImpl.kt`   | The most complex file in the data layer. The `getCompanyListings` implementation is worth reading carefully: it emits cached data first, decides whether a remote fetch is needed, saves favorited symbols before clearing the table, inserts fresh data, and restores favorites — all as a single Flow. |

---

## Layer 3 — DI

> The wiring. Shows how Hilt connects interfaces to their implementations.

| #  | File                    | Purpose                                                                                                                                  |
|----|-------------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| 25 | `di/AppModule.kt`       | Provides Moshi + Retrofit → `FinnhubApi`, OkHttp with body-level logging, and the Room database with `MIGRATION_1_2` registered. Exposes the DAO through the database instance rather than binding it separately. |
| 26 | `di/RepositoryModule.kt`| Single `@Binds` entry: `StockRepositoryImpl` → `StockRepository`. If more repositories are added, they go here.                          |

---

## Layer 4 — Presentation: Listings Screen

> The busiest screen. Handles search, tab switching, favorites, and watchlist quotes.

| #  | File                                              | Purpose                                                                                                                                   |
|----|---------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------|
| 27 | `presentation/company_listings/ListingsTab.kt`    | Two-value enum: `ALL` and `FAVORITES`. Exists as its own file to avoid a dependency cycle between state and event.                        |
| 28 | `presentation/company_listings/CompanyListingEvent.kt` | Sealed class of all user-driven actions. Read this to understand what the screen can do before reading the ViewModel.                |
| 29 | `presentation/company_listings/CompanyListingsState.kt` | Holds `companies` (the full search result), tab selection, error, quote map, and loading flags. The `displayedCompanies` computed property is the key insight: the Watchlist tab is just a client-side filter over the same list — no separate query. |
| 30 | `presentation/company_listings/CompanyListingsViewModel.kt` | Read `loadCompanyListings` first (the Flow collector and its post-completion hook), then `loadWatchlistQuotes` (the `async/awaitAll` concurrent quote fetch), then the event handler. The `ToggleFavorite` handler does an optimistic local state update in addition to writing to the DB so the star flips instantly. |
| 31 | `presentation/company_listings/CompanyItem.kt`    | Row composable. The `quote` parameter is nullable — when null (All tab) the price badge is absent; when non-null (Watchlist tab with loaded quote) it appears. |
| 32 | `presentation/company_listings/CompanyListingsScreen.kt` | Screen composable. Reading order within the file: `CompanyListingsScreen` (wires events and state), `WatchlistEmptyState` (the empty-watchlist illustration). |

---

## Layer 5 — Presentation: Detail Screen

> Two-phase load: company profile arrives first, quote arrives ~1 second later.

| #  | File                                          | Purpose                                                                                                                                               |
|----|-----------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| 33 | `presentation/company_info/CompanyInfoState.kt` | Four boolean/nullable fields. `isLoading` covers the initial full-screen skeleton; `isQuoteLoading` covers the second phase while the profile is visible but the quote isn't yet. |
| 34 | `presentation/company_info/CompanyInfoViewModel.kt` | Collects the Flow from `GetCompanyDetailUseCase`. The `Success(info, null)` emission sets `isQuoteLoading = true`; the subsequent `Success(info, quote)` clears it. `retry()` resets error state and re-runs `load()`. |
| 35 | `presentation/company_info/CompanyInfoScreen.kt` | Three top-level states: `isLoading → CompanyInfoSkeleton`, `error != null → CompanyInfoErrorState`, else `CompanyInfoContent`. Within `CompanyInfoContent`, the quote section is either `QuoteSkeleton` or the real data depending on `isQuoteLoading`. The shimmer brush is a horizontally sweeping `linearGradient` driven by `rememberInfiniteTransition`. |

---

## Layer 6 — Presentation: News Screen

> Simplest screen in the app — single-shot fetch, no caching, browser handoff.

| #  | File                                  | Purpose                                                                                                                   |
|----|---------------------------------------|---------------------------------------------------------------------------------------------------------------------------|
| 36 | `presentation/news/NewsState.kt`      | Four fields: `articles`, `isLoading`, `isRefreshing`, `errorMessage`. `isRefreshing` is distinct from `isLoading` so pull-to-refresh doesn't blank the article list. |
| 37 | `presentation/news/NewsViewModel.kt`  | Loads on init. `refresh()` sets `isRefreshing` and calls the private `loadNews(isRefresh = true)` to skip the initial loading spinner. |
| 38 | `presentation/news/NewsItem.kt`       | Article row: headline + source (coloured `primary`) + relative timestamp via `DateUtils.getRelativeTimeSpanString` + 2-line summary + 80 dp Coil thumbnail. |
| 39 | `presentation/news/NewsScreen.kt`     | `PullToRefreshBox` over a `LazyColumn`. Tapping a row fires `Intent.ACTION_VIEW` — the app never renders article content itself. |

---

## Layer 7 — Navigation, Shared UI, and Bootstrap

| #  | File                                       | Purpose                                                                                                                                              |
|----|--------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------|
| 40 | `presentation/navigation/Screen.kt`        | Three `@Serializable` route types: `CompanyListings`, `CompanyInfo(symbol)`, `MarketNews`. Type-safe navigation — no string routes anywhere in the app. |
| 41 | `presentation/navigation/NavGraph.kt`      | `NavHost` wiring the three routes to their screen composables.                                                                                        |
| 42 | `presentation/ui/PriceChangeBadge.kt`      | Shared composable used in both `CompanyItem` and `CompanyInfoScreen`. Computes delta and percent, picks `ProfitGreen` or `LossRed`, formats the string. |
| 43 | `presentation/ui/theme/Color.kt`           | Brand palette. The two most important constants for feature work are `ProfitGreen` and `LossRed` at the bottom — used whenever displaying price movement. |
| 44 | `presentation/ui/theme/Theme.kt`           | `StonksTheme` applies the full Material 3 color scheme. Dynamic color is intentionally disabled so the green palette always renders regardless of wallpaper. |
| 45 | `presentation/MainActivity.kt`             | Single activity. Hosts the `Scaffold` with the `NavigationBar`. The bar is hidden when `currentDestination.hasRoute<CompanyInfo>()` is true. Tab navigation uses `popUpTo + launchSingleTop` to keep the back stack flat. |
| 46 | `StonksApp.kt`                             | `@HiltAndroidApp` application class — just the bootstrap needed for Hilt to generate its component graph.                                             |
