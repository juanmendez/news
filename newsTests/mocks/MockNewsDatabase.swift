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

/// A hand-written mock for the NewsDatabase protocol.
/// Unlike SessionNewsDatabase, the articles store is an instance variable,
/// so each MockNewsDatabase instance has its own isolated state.
final class MockNewsDatabase: @unchecked Sendable, NewsDatabase {

    /// Read-only access from outside the class so tests can inspect what was
    /// saved to the database without being able to mutate it directly.
    /// Use `database.mapQueryArticles[query]` in assertions to verify that
    /// articles were persisted under the expected query key.
    private(set) var mapQueryArticles: [String: [ArticleEntity]]

    /// Allows tests to pre-populate the database with existing articles,
    /// simulating a cache-hit scenario where data is already available locally
    /// before any network request is made. Defaults to empty.
    init(mapQueryArticles: [String: [ArticleEntity]] = [:]) {
        self.mapQueryArticles = mapQueryArticles
    }

    func saveArticle(_ query: String, articleEntity: ArticleEntity) async throws {
        mapQueryArticles[query, default: []].append(articleEntity)
    }

    func readArticles(_ query: String) -> [ArticleEntity] {
        mapQueryArticles[query] ?? []
    }

    func deleteArticles(_ query: String) async throws {
        mapQueryArticles.removeValue(forKey: query)
    }

    static func setupConfiguration(_ configuration: inout GRDB.Configuration) { }
}
