//
//  DefaultNewsDatabase.swift
//  news
//
//  Created by Mendez, Juan on 11/6/25.
//
import Foundation
import GRDB

struct DefaultNewsDatabase: NewsDatabase {
    // great learning from https://swiftpackageindex.com/groue/grdb.swift/v7.8.0/documentation/grdb/
    static func create() throws -> NewsDatabase {
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

    private let dbWriter: any DatabaseWriter

    private init(_ dbWriter: any GRDB.DatabaseWriter) throws {
        self.dbWriter = dbWriter
        try migrator.migrate(dbWriter)
    }

    func saveArticle(_ articleEntity: ArticleEntity) {
        do {
            try dbWriter.write { database in
                try articleEntity.insert(database)
            }
        } catch {
            Log.e("Failed to save article: \(error)")
        }
    }

    func readArticles() -> [ArticleEntity] {
        var articles = [ArticleEntity]()
        do {
            try dbWriter.read { database in
                articles.append(contentsOf: try ArticleEntity.fetchAll(database))
            }
        } catch {
            Log.e("Failed to fetch articles: \(error)")
        }

        return articles
    }

    static func setupConfiguration(_ configuration: inout GRDB.Configuration) {
        #if DEBUG
            configuration.prepareDatabase { database in
                database.trace { event in
                    Log.i("SQL> \(event)")
                }
            }
        #endif
    }

    private var migrator: DatabaseMigrator {
        var migrator = DatabaseMigrator()
        migrator.eraseDatabaseOnSchemaChange = true

        migrator.registerMigration("v1") { database in
            try database.create(
                table: "articleEntity",
                body: { definition in
                    definition.column("id", .text).primaryKey()  // UUID stored as text
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
        }

        return migrator
    }
}
