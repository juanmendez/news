# Database

## Overview
- Database layer uses **GRDB** (via CocoaPods)
- Protocol: `NewsDatabase`
- Implementation: `DefaultNewsDatabase`
- Session stub: `SessionNewsDatabase` (in-memory, for previews and tests)

## Models
- Conform to `FetchableRecord`, `MutablePersistableRecord`, `Codable`
- Auto-increment primary keys use `var id: Int64?` (optional — SQLite assigns the value)
- Implement `didInsert(with:for:)` to capture the generated id after insert:
  ```swift
  mutating func didInsert(with rowID: Int64, for column: String?) {
      id = rowID
  }
  ```

## Tables

### `articleEntity`
| Column | Type | Notes |
|--------|------|-------|
| `id` | `TEXT` | Primary key (article URL or title) |
| `sourceId` | `TEXT?` | Nullable |
| `sourceName` | `TEXT` | |
| `author` | `TEXT` | |
| `title` | `TEXT` | |
| `description` | `TEXT` | |
| `url` | `TEXT` | |
| `imageUrl` | `TEXT` | |
| `publishedAt` | `INTEGER` | Unix timestamp in milliseconds (Int64) |
| `content` | `TEXT` | |

### `queryArticleEntity`
| Column | Type | Notes |
|--------|------|-------|
| `id` | `INTEGER` | Auto-increment primary key |
| `queryName` | `TEXT` | Search query string |
| `articleId` | `TEXT` | Foreign key → `articleEntity.id` |

- Composite unique key on `(queryName, articleId)` with `onConflict: .ignore` — prevents duplicate links

## Migrations
- Versioned: `v1`, `v2`, etc.
- **Never modify existing migrations** — always add a new version
- Example:
  ```swift
  migrator.registerMigration("v1") { db in
      try db.create(table: "articleEntity") { t in
          t.column("id", .text).primaryKey()
          // ...
      }
  }
  migrator.registerMigration("v2") { db in
      try db.create(
          index: "uq_queryArticle",
          on: "queryArticleEntity",
          columns: ["queryName", "articleId"],
          unique: true
      )
  }
  ```

## Inserting Records
- Always omit `id` for auto-increment tables — let SQLite assign it:
  ```swift
  // ✅ Correct
  var link = QueryArticleEntity(queryName: "Top Headlines", articleId: article.id)
  try link.insert(database)

  // ❌ Avoid
  var link = QueryArticleEntity(id: 0, queryName: "Top Headlines", articleId: article.id)
  ```

## Reading Records
- Articles are keyed by query — always pass the query string when reading:
  ```swift
  database.readArticles("Top Headlines")  // returns [ArticleEntity]
  ```

## SessionNewsDatabase (In-Memory)
- Used for SwiftUI previews and unit tests
- Articles stored in a static `[String: [ArticleEntity]]` dictionary keyed by query:
  ```swift
  private static var articles: [String: [ArticleEntity]] = [:]
  ```
- `saveArticle(_:articleEntity:)` — appends to the array for the given query key
- `readArticles(_:)` — returns articles for the given query key, or `[]` if none

## Date Handling
- `publishedAt` is stored as `Int64` (Unix timestamp in **milliseconds**)
- Convert to `Date`:
  ```swift
  let date = Date(timeIntervalSince1970: TimeInterval(publishedAt) / 1000.0)
  ```
- `ArticleEntity` provides convenience computed properties:
  - `publishedDate: Date`
  - `formattedPublishedDate(style:) -> String`
  - `relativePublishedTime: String`
