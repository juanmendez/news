//
//  MockNewsDatabase.swift
//  newsTests
//
//  Created by Mendez, Juan on 3/3/26.
//

import Foundation
import GRDB

@testable import news

// MARK: - MockNewsDatabase

/// A test double for `NewsDatabase` backed by a real in-memory GRDB instance.
/// Each `MockNewsDatabase` instance gets its own isolated SQLite database,
/// so tests never share state.
@MainActor
final class MockNewsDatabase: NewsDatabase {
    private let database: NewsDatabase

    /// Creates a new isolated in-memory database instance.
    /// - Throws: If the in-memory database or migrations fail to initialize.
    init() throws {
        database = try DefaultNewsDatabase.create(.inMemory)
    }

    func saveArticle(_ query: String, articleEntity: ArticleEntity) async throws {
        try await database.saveArticle(query, articleEntity: articleEntity)
    }

    func readArticles(_ query: String) async -> [ArticleEntity] {
        await database.readArticles(query)
    }

    func deleteArticles(_ query: String) async throws {
        try await database.deleteArticles(query)
    }

    /// Pre-populates the database with a list of articles under the given query.
    /// Use this in tests to simulate a cache-hit scenario before any network request is made.
    /// - Parameters:
    ///   - query: The search query to associate the articles with.
    ///   - articles: The articles to persist.
    func preload(_ query: String, articles: [ArticleEntity]) async throws {
        for article in articles {
            try await database.saveArticle(query, articleEntity: article)
        }
    }

    func readQueries() async -> [news.QueryEntity] {
        await database.readQueries()
    }

    static func setupConfiguration(_ configuration: inout GRDB.Configuration) { }
}
