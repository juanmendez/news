//
//  DefaultNewsDatabase.swift
//  news
//
//  Created by Mendez, Juan on 11/6/25.
//
import Foundation
import GRDB

/// Defines the storage strategy for `DefaultNewsDatabase`.
enum DatabaseType {
    /// Persists data to a SQLite file in the app's Application Support directory.
    case persistent
    /// Stores data in memory only — isolated per instance, ideal for unit tests.
    case inMemory
}

/// A struct that conforms to the `NewsDatabase` protocol, providing an implementation
/// backed by a SQLite database using the GRDB library.
struct DefaultNewsDatabase: NewsDatabase {
    // great learning from https://swiftpackageindex.com/groue/grdb.swift/v7.8.0/documentation/grdb/

    /// Creates and returns a fully migrated `DefaultNewsDatabase` instance.
    /// - Parameter type: The storage strategy — `.persistent` for production, `.inMemory` for tests.
    /// - Throws: Any error from file system operations or database initialization.
    /// - Returns: A ready-to-use `NewsDatabase` instance.
    static func create(_ type: DatabaseType = .persistent) throws -> NewsDatabase {
        switch type {
            case .persistent:
                return try makePersistent()
            case .inMemory:
                return try makeInMemory()
        }
    }

    // MARK: - Private Factory Helpers

    private static func makePersistent() throws -> NewsDatabase {
        let fileManager = FileManager.default
        let appSupportURL = try fileManager.url(
            for: .applicationSupportDirectory,
            in: .userDomainMask,
            appropriateFor: nil,
            create: true
        )
        let directoryURL = appSupportURL.appendingPathComponent("Database", isDirectory: true)
        try fileManager.createDirectory(at: directoryURL, withIntermediateDirectories: true)

        // Open or create the database
        let databaseURL = directoryURL.appendingPathComponent("db.sqlite")

        var configuration = Configuration()
        DefaultNewsDatabase.setupConfiguration(&configuration)

        let databaseQueue = try DatabasePool(path: databaseURL.path, configuration: configuration)
        return try DefaultNewsDatabase(databaseQueue)
    }

    private static func makeInMemory() throws -> NewsDatabase {
        let databaseQueue = try DatabaseQueue()
        return try DefaultNewsDatabase(databaseQueue)
    }

    private let dbWriter: any DatabaseWriter

    private init(_ dbWriter: any GRDB.DatabaseWriter) throws {
        self.dbWriter = dbWriter
        try migrator.migrate(dbWriter)
    }

    /// Persists a single article under the given query key.
    /// Inserts the `QueryEntity` (if not already present), the `QueryArticleEntity` join record,
    /// and the `ArticleEntity` itself — all within a single write transaction.
    /// - Parameters:
    ///   - query: The search query the article belongs to.
    ///   - articleEntity: The article to persist.
    func saveArticle(_ query: String, articleEntity: ArticleEntity) async throws {
        try await dbWriter.write { database in
            try QueryEntity(queryName: query).insert(database)
            try QueryArticleEntity(queryName: query, articleId: articleEntity.id).insert(database)
            try articleEntity.insert(database)
        }
    }

    /// Reads all articles associated with the given query from the database.
    /// Resolves the query → article join via `QueryArticleEntity` and returns
    /// the matching `ArticleEntity` records.
    /// - Parameter query: The search query to look up.
    /// - Returns: An array of `ArticleEntity` records, or empty if none are found.
    func readArticles(_ query: String) async -> [ArticleEntity] {
        do {
            let articles: [ArticleEntity] = try await dbWriter.read { database in
                if let queryEntity = try? QueryEntity.find(database, key: query),
                   let articleIds = try? QueryArticleEntity.filter({ $0.queryname == queryEntity.queryName }).fetchAll(database).map(\.articleId) {

                    return try ArticleEntity.filter { columns in
                        articleIds.contains(columns.id)
                    }.fetchAll(database)
                }

                return []
            }
            return articles
        } catch {
            Log.e("Failed to fetch articles: \(error)")
            return []
        }
    }

    /// Deletes all data associated with the given query in the correct dependency order:
    /// 1. Removes the `QueryEntity` row for the query.
    /// 2. Collects the associated article IDs before removing the join records.
    /// 3. Removes all `QueryArticleEntity` associations for the query.
    /// 4. Filters article IDs down to only those no longer referenced by any other query.
    /// 5. Deletes the remaining unreferenced `ArticleEntity` rows.
    /// - Parameter query: The search query whose data should be deleted.
    func deleteArticles(_ query: String) async throws {
        try await dbWriter.write { database in
            // Remove the query entry from QueryEntity
            try QueryEntity.deleteOne(database, key: query)

            // Collect all article IDs associated with this query before removing the associations
            var articleIds =
            try QueryArticleEntity
                .filter({ $0.queryname == query })
                .fetchAll(database)
                .map(\.articleId)

            // Remove all associations between this query and its articles
            try QueryArticleEntity
                .filter({ $0.queryname == query })
                .deleteAll(database)

            // Keep only articles that are no longer referenced by any other query
            articleIds = articleIds.filter { articleId in
                let count =
                (try? QueryArticleEntity
                    .filter({ $0.articleId == articleId })
                    .fetchCount(database)) ?? 0
                return count == 0
            }

            // Delete articles that are exclusively associated with this query
            try articleIds.forEach { articleId in
                try ArticleEntity.deleteOne(database, key: articleId)
            }
        }
    }

    /// Configures GRDB tracing in DEBUG builds to log all SQL statements.
    /// - Parameter configuration: The GRDB `Configuration` to apply settings to.
    static func setupConfiguration(_ configuration: inout GRDB.Configuration) {
        //        #if DEBUG
        //            configuration.prepareDatabase { database in
        //                database.trace { event in
        //                    Log.i("SQL> \(event)")
        //                }
        //            }
        //        #endif
    }

    private var migrator: DatabaseMigrator {
        var migrator = DatabaseMigrator()
        migrator.eraseDatabaseOnSchemaChange = true

        migrator.registerMigration("v1") { database in
            try database.create(
                table: "articleEntity",
                body: { definition in
                    definition.column("id", .text).primaryKey(onConflict: .replace)
                    definition.column("sourceId", .text)
                    definition.column("sourceName", .text).notNull()
                    definition.column("author", .text).notNull()
                    definition.column("title", .text).notNull()
                    definition.column("description", .text).notNull()
                    definition.column("url", .text).notNull()
                    definition.column("imageUrl", .text).notNull()
                    definition.column("publishedAt", .integer).notNull()  // Int64
                    definition.column("content", .text).notNull()
                }
            )

            try database.create(
                table: "queryEntity",
                body: { definition in
                    definition.column("queryName", .text).primaryKey(onConflict: .replace).notNull()
                }
            )

            try database.create(
                table: "queryArticleEntity",
                body: { definition in
                    definition.autoIncrementedPrimaryKey("id")
                    definition.column("queryName", .text).notNull()
                    definition.column("articleId", .text).notNull()
                }
            )
        }

        return migrator
    }

    func readQueries() async -> [QueryEntity] {
        let articles = try? await dbWriter.read { database in
            try? QueryEntity.fetchAll(database)
        }
        
        return articles ?? []
    }
}
