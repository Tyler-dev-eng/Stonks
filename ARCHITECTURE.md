# Architecture

Stonks follows **Clean Architecture** with three layers: `data`, `domain`, and `presentation`. Each layer has a strict dependency direction — `presentation` depends on `domain`, `data` depends on `domain`, and `domain` depends on nothing.

```
app/src/main/java/com/tylerdev/stonks/
├── StonksApp.kt
├── data/
│   ├── local/
│   │   ├── dao/
│   │   └── entity/
│   ├── mapper/
│   ├── remote/
│   │   ├── api/
│   │   └── dto/
│   └── repository/
├── di/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
├── presentation/
│   ├── company_info/
│   ├── company_listings/
│   ├── navigation/
│   ├── news/
│   └── ui/
│       └── theme/
└── util/
```

---

## Root

| File           | Purpose                                                                                                                         |
|----------------|---------------------------------------------------------------------------------------------------------------------------------|
| `StonksApp.kt` | `Application` subclass annotated with `@HiltAndroidApp`. Required for Hilt to generate the component graph from modules in `di/`. |

---

## `data/`

Responsible for all I/O. Nothing in this layer leaks into `domain` or `presentation` — remote DTOs and Room entities are mapped into domain models before crossing the boundary.

### `data/local/`

Room database, DAO, and entity for the locally cached company listing data.

| File                       | Purpose                                                                                                                                                                                                             |
|----------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `StockDatabase.kt`         | Room database class registering `CompanyListingEntity`. Currently at version 2. Contains `MIGRATION_1_2` which adds the `isFavorite` column via `ALTER TABLE` so existing installs preserve their cached data.       |
| `dao/StockDao.kt`          | DAO interface for all listing persistence: bulk insert/replace, full clear, search by name or exact symbol, toggle favorite flag by symbol, and helpers to save/restore the set of favorited symbols across a refresh. |
| `entity/CompanyListingEntity.kt` | Room entity representing one cached company listing. Holds `name`, `symbol`, `exchange`, and `isFavorite` (added in migration 2). Uses an auto-increment `id` primary key.                                    |

### `data/mapper/`

Extension functions that translate between remote DTOs / Room entities and domain models. Keeping this logic here prevents it leaking into repositories or DTOs.

| File               | Purpose                                                                                                                                                                                                                           |
|--------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `CompanyMapper.kt` | `CompanyListingEntity → CompanyListingDomainModel` and the reverse. `FinnhubSymbolDto → CompanyListingDomainModel` (nullable; returns null if symbol or name is missing). `FinnhubProfileDto → CompanyInfoDomainModel`. `FinnhubQuoteDto → StockQuoteDomainModel`. |
| `NewsMapper.kt`    | `FinnhubNewsDto → NewsArticleDomainModel` (nullable; returns null if headline or URL is blank so malformed articles are silently dropped).                                                                                          |

### `data/remote/`

Retrofit interface and Moshi DTOs for the [Finnhub](https://finnhub.io) API.

#### `data/remote/api/`

| File            | Purpose                                                                                                                                                                         |
|-----------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `FinnhubApi.kt` | Retrofit interface declaring four endpoints: `GET stock/symbol` (US exchange listing), `GET stock/profile2` (company profile), `GET quote` (real-time price), `GET news` (general market news). All endpoints inject the API key from `BuildConfig`. |

#### `data/remote/dto/`

| File                  | Purpose                                                                                                                     |
|-----------------------|-----------------------------------------------------------------------------------------------------------------------------|
| `FinnhubSymbolDto.kt` | Moshi DTO for a single row from the symbols endpoint. Fields: `symbol`, `description` (company name), `mic` (exchange MIC). |
| `FinnhubProfileDto.kt`| Moshi DTO for the company profile endpoint. Fields: `ticker`, `name`, `country`, `finnhubIndustry`, `logo`.                 |
| `FinnhubQuoteDto.kt`  | Moshi DTO for the quote endpoint. Fields: `c` (current), `h` (high), `l` (low), `o` (open), `pc` (previous close).         |
| `FinnhubNewsDto.kt`   | Moshi DTO for a single news article. Fields: `id`, `headline`, `summary`, `source`, `image`, `url`, `datetime`, `category`. |

All DTO constructor parameters use `@param:Json` to explicitly scope the Moshi annotation to the parameter, avoiding the Kotlin annotation-target ambiguity warning.

### `data/repository/`

| File                    | Purpose                                                                                                                                                                                                                                                                                                  |
|-------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `StockRepositoryImpl.kt`| Implements `StockRepository`. `getCompanyListings` emits a loading state, immediately emits cached data, then conditionally fetches from remote — preserving favorited symbols across the wipe-and-reload cycle. `toggleFavorite` delegates directly to the DAO. `getStockQuote`, `getCompanyInfo`, and `getMarketNews` are single-shot suspend calls wrapped in `try/catch` for `IOException` and `HttpException`. |

---

## `di/`

Hilt modules that wire the dependency graph. All modules install into `SingletonComponent`.

| File                  | Purpose                                                                                                                                                                                     |
|-----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `AppModule.kt`        | Provides the Retrofit-backed `FinnhubApi` (with OkHttp body logging), the singleton `StockDatabase` (with `MIGRATION_1_2` registered), and exposes the DAO through the database instance.  |
| `RepositoryModule.kt` | Binds `StockRepositoryImpl` as the singleton implementation of `StockRepository`.                                                                                                          |

**What goes here as the project grows:** a `DatabaseModule` if the database grows complex enough to warrant separation; additional `@Binds` entries for new repository implementations.

---

## `domain/`

Pure Kotlin — no Android framework imports. `data` and `presentation` both depend on this layer; it depends on neither.

### `domain/model/`

| File                         | Purpose                                                                                                                                                                                 |
|------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `CompanyListingDomainModel`  | A single row from the listings cache: `name`, `symbol`, `exchange`, and `isFavorite`. The source of truth for both the listings list and the watchlist tab.                             |
| `CompanyInfoDomainModel`     | Profile data for a specific company: `symbol`, `name`, `country`, `industry`, `description`, `logoUrl`.                                                                                 |
| `StockQuoteDomainModel`      | Real-time price snapshot: `current`, `high`, `low`, `open`, `previousClose`. Delta and percentage change are computed on-the-fly from `current` and `previousClose` in the UI layer.    |
| `CompanyDetailDomainModel`   | Aggregation type used internally by `GetCompanyDetailUseCase`. Bundles `CompanyInfoDomainModel` with an optional `StockQuoteDomainModel` so the detail flow can emit partial state early. |
| `NewsArticleDomainModel`     | A single market news article: `id`, `headline`, `summary`, `source`, `imageUrl`, `url`, `datetimeEpochSeconds`, `category`.                                                             |

### `domain/repository/`

| File                | Purpose                                                                                                                                                                                                   |
|---------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `StockRepository.kt`| The single domain contract for all stock and news data. Declares `getCompanyListings` (Flow-based for cache-then-network), `toggleFavorite`, `getStockQuote`, `getCompanyInfo`, and `getMarketNews`.       |

### `domain/usecase/`

Each use case has a single responsibility and is invoked via `operator fun invoke()`.

| File                        | Purpose                                                                                                                                                                                                                                              |
|-----------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `GetCompanyListingsUseCase` | Delegates to `StockRepository.getCompanyListings`, threading through `fetchFromRemote` and `query` parameters.                                                                                                                                       |
| `GetCompanyInfoUseCase`     | Single-shot fetch of company profile data via `StockRepository.getCompanyInfo`.                                                                                                                                                                      |
| `GetStockQuoteUseCase`      | Single-shot fetch of a real-time quote via `StockRepository.getStockQuote`.                                                                                                                                                                          |
| `GetCompanyDetailUseCase`   | Orchestration use case that composes `GetCompanyInfoUseCase` and `GetStockQuoteUseCase` into a `Flow`. Emits `Loading`, then `Success(info, null)` immediately after profile loads, then waits 1100 ms (Finnhub free-tier rate limit) before fetching and emitting the quote. |
| `GetMarketNewsUseCase`      | Single-shot fetch of general market news via `StockRepository.getMarketNews`.                                                                                                                                                                        |
| `ToggleFavoriteUseCase`     | Writes the new `isFavorite` state for a given symbol via `StockRepository.toggleFavorite`.                                                                                                                                                           |

---

## `presentation/`

Jetpack Compose UI layer. ViewModels hold `StateFlow<ScreenState>` and delegate all business logic to use cases. No Retrofit or Room types appear here.

### `presentation/MainActivity.kt`

Single-activity entry point. Hosts a `Scaffold` with a `NavigationBar` (Markets + News tabs). The bottom bar is hidden when the current destination is `CompanyInfo` so the detail screen gets the full viewport. Tab navigation uses `popUpTo` + `launchSingleTop` to avoid back-stack accumulation.

### `presentation/company_listings/`

| File                        | Purpose                                                                                                                                                                                                                                                                 |
|-----------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `CompanyListingsState.kt`   | UI state for the listings screen. Holds `companies` (full search result from the DB), `isLoading`, `isRefreshing`, `searchQuery`, `errorMessage`, `selectedTab`, and `quotesBySymbol` (map of symbol → quote for watchlist rows). Exposes a computed `displayedCompanies` that filters by `isFavorite` on the Watchlist tab. |
| `CompanyListingEvent.kt`    | Sealed class of all user actions: `Refresh`, `OnSearchQueryChange`, `ErrorDismissed`, `SelectTab`, `ToggleFavorite`.                                                                                                                                                     |
| `ListingsTab.kt`            | `enum class ListingsTab { ALL, FAVORITES }` — the two tab states.                                                                                                                                                                                                       |
| `CompanyListingsViewModel`  | Injects `GetCompanyListingsUseCase`, `GetStockQuoteUseCase`, and `ToggleFavoriteUseCase`. Handles debounced search (500 ms), pull-to-refresh, watchlist tab selection, and favorite toggling. When the Watchlist tab is active, fetches quotes for all favorited symbols concurrently via `async/awaitAll`. |
| `CompanyListingsScreen.kt`  | Search field → `TabRow` (All / Watchlist) → `PullToRefreshBox` wrapping a `LazyColumn` of `CompanyItem` rows. Shows `WatchlistEmptyState` when the Watchlist tab is selected and no favorites exist. Errors surface via `Snackbar`.                                     |
| `CompanyItem.kt`            | A single listing row: company name + exchange on the top line; ticker symbol + optional `PriceChangeBadge` (shown only when a quote is available) on the bottom line; star `IconButton` on the trailing edge to toggle favorites.                                        |

### `presentation/company_info/`

| File                    | Purpose                                                                                                                                                                                                              |
|-------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `CompanyInfoState.kt`   | UI state for the detail screen. Holds `company`, `quote`, `isLoading`, `isQuoteLoading`, and `error`.                                                                                                                 |
| `CompanyInfoViewModel`  | Collects the `Flow` from `GetCompanyDetailUseCase`, mapping each emission to state. Sets `isQuoteLoading = true` after profile loads but before quote arrives. Exposes `retry()` to re-run the load from the error state. |
| `CompanyInfoScreen.kt`  | Renders one of three states: full shimmer skeleton (`isLoading`), `CompanyInfoErrorState` (`error != null`), or `CompanyInfoContent`. Content shows logo + name + country/industry, then either a quote skeleton or the real price + `PriceChangeBadge` + four stat columns. The error state shows a `SearchOff` icon, a descriptive message, and a retry button. |

### `presentation/news/`

| File             | Purpose                                                                                                                                                                         |
|------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `NewsState.kt`   | UI state for the news screen. Holds `articles`, `isLoading`, `isRefreshing`, `errorMessage`.                                                                                    |
| `NewsViewModel`  | Loads articles on init via `GetMarketNewsUseCase`. Exposes `refresh()` which sets `isRefreshing` without blanking the existing article list.                                    |
| `NewsScreen.kt`  | `PullToRefreshBox` wrapping a `LazyColumn` of `NewsItem` rows under a "Market News" header. Tapping any row fires `Intent.ACTION_VIEW` to open the article in the system browser. Errors shown via `Snackbar`. |
| `NewsItem.kt`    | A single article row: headline (3 lines, semi-bold) + source in `primary` colour + relative timestamp via `DateUtils.getRelativeTimeSpanString` + 2-line summary; 80 dp thumbnail on the trailing edge loaded with Coil. |

### `presentation/navigation/`

| File           | Purpose                                                                                                                                                        |
|----------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `Screen.kt`    | Type-safe navigation routes: `CompanyListings` (data object), `CompanyInfo(symbol: String)` (data class), `MarketNews` (data object). All annotated `@Serializable`. |
| `NavGraph.kt`  | `NavHost` wiring the three routes to their screens. `CompanyListings` passes an `onCompanyClick` lambda that navigates to `CompanyInfo`.                        |

### `presentation/ui/`

| File                  | Purpose                                                                                                                                                                               |
|-----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `PriceChangeBadge.kt` | Reusable composable used on both the detail screen and watchlist rows. Computes `change = current − previousClose` and `changePct`, formats as `+2.34 (+1.20%)`, and colours the text `ProfitGreen` or `LossRed`. |

#### `presentation/ui/theme/`

| File       | Purpose                                                                                                                                                                                                   |
|------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `Color.kt` | Brand color palette: a green scale (`Green10`–`Green95`), muted `GreenGrey` neutrals, `Teal` accent, dark/light surface tokens, and two semantic constants — `ProfitGreen` (`#1DB954`) and `LossRed` (`#E53935`). |
| `Theme.kt` | `StonksTheme` composable. Defines full Material 3 `lightColorScheme` and `darkColorScheme` using the brand palette. Dynamic color is intentionally disabled so the custom palette always renders.          |
| `Type.kt`  | Typography scale.                                                                                                                                                                                         |

---

## `util/`

| File          | Purpose                                                                                                                                                                                          |
|---------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `Resource.kt` | Sealed class wrapping async operation results into `Success<T>`, `Loading<T>`, and `Error<T>`. Repositories return `Resource<T>`; ViewModels map each variant to the appropriate UI state field. |

---

## `res/`

### `res/values/` and `res/values-night/`

`colors.xml` defines `brand_surface_light` (`#F6FAF7`) and `brand_surface_dark` (`#191C1A`). `themes.xml` sets `android:windowBackground` to `brand_surface_light` so the OS-drawn window matches the app's light background before Compose renders. The `values-night/` override uses `brand_surface_dark` to eliminate the flash on cold start in dark mode.

### `res/mipmap-*/`

Launcher icons at each screen density.

### `res/xml/`

`backup_rules.xml` and `data_extraction_rules.xml` — Android backup and data-extraction configuration.
