## Testing
- When asked to **create or modify unit tests**, do **not** make any changes to the unit of software (ViewModel, Repository, etc.) being tested — only modify the test file
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
- **Do not request or require the user to run tests after making changes to test files.**

