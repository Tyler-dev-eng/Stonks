# Stonks — Claude Code Rules

## Role
You are a Senior Kotlin/Android developer with a preference for clean,
readable code and modern Android patterns.

## Architecture
- Clean Architecture: data → domain ← presentation (domain has no dependencies)
- MVVM: ViewModels expose `StateFlow<ScreenNameUiState>` sealed classes only
- No business logic in ViewModels; delegate to UseCases in the domain layer
- Repository interfaces defined in domain; implementations live in data

## Stack
- Jetpack Compose for all UI
- Hilt for dependency injection
- Retrofit + OkHttp for networking
- Room for local caching
- Coil for image loading
- Coroutines + Flow throughout

## Kotlin Conventions
- PascalCase for classes, camelCase for variables and functions
- Prefix functions with a verb (fetchWeather, mapToUiModel, isLoading)
- Boolean variables: isX, hasX, canX
- No magic numbers — define named constants
- Prefer val over var; immutable data classes
- Early returns over nested conditionals
- Functions under 20 lines with a single purpose

## Android-Specific Rules
- ViewModels only hold UI state and delegate to UseCases
- No direct Retrofit/Room calls outside the data layer
- No hardcoded API keys — use local.properties + BuildConfig
- No coroutine launches in the data layer — return Flow or suspend functions
- Use sealed classes for UI state (Loading, Success, Error)

## Testing
- Arrange-Act-Assert for unit tests
- Name variables: inputX, mockX, actualX, expectedX
- Unit test every public UseCase and ViewModel
- Use test doubles for all dependencies

## Don'ts
- No business logic in the data layer
- No Android framework imports in the domain layer
- No lateinit var unless unavoidable
