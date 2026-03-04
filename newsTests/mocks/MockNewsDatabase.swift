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
    private var articles: [String: [ArticleEntity]] = [:]

    func saveArticle(_ query: String, articleEntity: ArticleEntity) {
        articles[query, default: []].append(articleEntity)
    }

    func readArticles(_ query: String) -> [ArticleEntity] {
        articles[query] ?? []
    }

    static func setupConfiguration(_ configuration: inout GRDB.Configuration) { }
}
