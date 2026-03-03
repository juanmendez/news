# Copilot Instructions

## Xcode File Management
- **Never** ask the user to run a command line to add new files to Xcode
- Instead, **list all newly created files** with their relative paths at the end of your response so the user can add them manually in Xcode:
  ```
  Files to add in Xcode:
  - news/app/views/articles/ArticlesView.swift
  - newsShared/constants/LocalizedStrings.swift
  ```
- Always include the target membership recommendation next to each file:
  ```
  Files to add in Xcode:
  - newsShared/constants/LocalizedStrings.swift  → add to: news, newsTests
  - news/app/views/articles/SearchView.swift     → add to: news
  ```

## Project
iOS app written in SwiftUI targeting iOS 17+. Originally ported from an Android/Kotlin codebase.

## Architecture
- **MVVM** with `@Observable` view models
- **Protocol-based contracts** for all ViewModels (e.g. `ArticlesViewModelContract`)
- **Dependency Injection** via `InjectionProvider` using `register` / `byType`
- **Repository pattern** — `DefaultRepository` → `DefaultHttpClient` + `DefaultNewsDatabase`
- **NetworkBoundResource** via `ResourceProvider.networkBoundResource` returning `AsyncStream<Resource<T>>`

## Mocking (Tests)
- No third-party mock frameworks (Mockingbird was removed)
- Hand-written mocks live in `newsTests/`
- Mock types are named `Mock{Type}` — e.g. `MockRepository`, `MockHttpClient`, `MockInternetService`
- Mock dependencies use **plain variables**, not closures:
  ```swift
  // ✅ Preferred
  var articlesStream: AsyncStream<Resource<ArticleEntities>>?
  var articlesRefreshStream: AsyncStream<Resource<ArticleEntities>>?
  var rawResponse: HttpClientResponseRaw?

  // ❌ Avoid
  var getArticlesHandler: ((...) async -> AsyncStream<...>)?
  ```

## Testing
- Use **Swift Testing** framework (`@Test`, `#expect`) — not XCTest
- Each `@Test` method gets its own struct instance, so shared properties are safe
- Use `AsyncStream.collect()` from `Asyncstream+Common.swift` to gather emissions
- Mock variables are set directly before calling the method under test:
  ```swift
  sut.articlesStream = AsyncStream { continuation in
      continuation.yield(.SUCCESS(item: articles))
      continuation.finish()
  }
  ```

## Code Style
- Prefer `AsyncStream` over callbacks or Combine
- Use `async/await` throughout — no completion handlers
- One class/struct per file
- Avoid force unwrapping (`!`)
- Use `if let` or `guard let` for optional unwrapping
- Break long strings into multiple lines using triple-quoted strings with backslash continuation:
  ```swift
  // ✅ Preferred
  let title = """
      Bye-bye bots: Altera's game-playing AI agents \
      get backing from Eric Schmidt | TechCrunch
      """

  // ❌ Avoid
  let title = "Bye-bye bots: Altera's game-playing AI agents get backing from Eric Schmidt | TechCrunch"
  ```

## Access Control
- Prefer `internal` (default) within the same target
- Use `public` only when sharing across module boundaries (e.g. framework targets)
- Prefer **target membership** over `public` for simple code sharing within the same project

## Shared Code
- `newsShared/` contains stubs, constants, and extensions shared across targets via target membership
- `newsShared/stubs/` — stub instances for previews and tests (e.g. `ArticleEntity+Stub.swift`, `Article+Stub.swift`)
- `newsShared/constants/` — shared constants like `Dimensions`, `PreviewConstants`

## Localization
- All user-facing strings are stored in `news/app/localization/Localizable.xcstrings`
- **Never** use `NSLocalizedString`
- In non-SwiftUI code (ViewModels, helpers, etc.), use `String(localized:)`:
  ```swift
  // ✅ Preferred
  self.errorMessage = String(localized: "something_went_wrong")

  // ❌ Avoid
  self.errorMessage = NSLocalizedString("something_went_wrong", comment: "")
  ```
- In SwiftUI components that accept `LocalizedStringKey` (e.g. `Text`, `Button`, `Label`, `TextField`, `.navigationTitle`, `.searchable(prompt:)`), pass the key as a plain string literal — SwiftUI resolves it automatically from `Localizable.xcstrings`:
  ```swift
  // ✅ Preferred
  Text("article_title")
  Button("search") { }
  .navigationTitle("app_name")
  TextField("search_hint", text: $query)

  // ❌ Avoid
  Text(String(localized: "article_title"))
  Text(LocalizedStrings.search)
  ```

## SwiftUI Conventions
- Use `NavigationStack` (not `NavigationView`)
- Use `.listStyle(.plain)` on all `List` views
- Use `Dimensions` constants for spacing, padding, and font sizes (mirrors Android `dimens.xml`)
- Use `AsyncImage` with `.resizable().scaledToFit()` for remote images
- Use `ArticleCardView` for rendering article rows

## File Naming
- Views: `ArticlesView.swift`
- ViewModels: `ArticlesViewModel.swift`
- ViewModel previews: `ArticlesViewModelPreview.swift`
- Mocks: `Mock{Type}.swift` — e.g. `MockRepository.swift`, `MockHttpClient.swift`, `MockInternetService.swift`
- Tests: `RepositoryTests.swift`, `HttpClientTest.swift`
- Stubs: `ArticleEntity+Stub.swift`, `Article+Stub.swift`
- Extensions: `String+Common.swift`, `Asyncstream+Common.swift`

## Database (GRDB)
- See [copilot-database.md](.github/copilot-database.md) for full details
- Models conform to `FetchableRecord`, `MutablePersistableRecord`, `Codable`
- Auto-increment primary keys use `var id: Int64?` (optional)
- Migrations are versioned: `v1`, `v2`, etc. — **never modify existing migrations**

## Networking
- See [copilot-networking.md](.github/copilot-networking.md) for full details
- All requests go through `DefaultHttpClient.rawRequest`
- Errors are normalized into `HttpError` cases: `.invalidUrl`, `.badResponse`, `.transport`, `.noHttpResponse`, `.other`
- `HttpRouter` defines endpoints
- All async data flows use `Resource<T>` — `LOADING`, `SUCCESS`, `ERROR`

## Dependency Injection
- Register dependencies in `InjectionProvider`:
  ```swift
  InjectionProvider.register { DefaultRepository() as Repository }
  ```
- Inject with `@Inject`:
  ```swift
  @Inject private var repository: Repository
  ```
