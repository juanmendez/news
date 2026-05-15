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

