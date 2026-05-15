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

