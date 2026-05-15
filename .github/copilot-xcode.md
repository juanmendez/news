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

