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

