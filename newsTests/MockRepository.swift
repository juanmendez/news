//
//  MockRepository.swift
//  news
//
//  Created by Mendez, Juan on 2/24/26.
//


import Foundation
@testable import news

// MARK: - MockRepository

/// A hand-written mock for the Repository protocol.
/// Use `getArticlesHandler` to control what each call returns.
final class MockRepository: Repository {

    // MARK: - Stub streams

    /// Set this before calling getArticles(query:page:) to control what the mock returns.
    var articlesStream: AsyncStream<Resource<[ArticleEntity]>> = AsyncStream { $0.finish() }

    /// Set this before calling getArticles(query:page:refresh:) to control what the mock returns.
    var articlesRefreshStream: AsyncStream<Resource<[ArticleEntity]>> = AsyncStream { $0.finish() }

    // MARK: - getArticles(query:page:)

    func getArticles(query: String, page: Int) -> AsyncStream<Resource<[ArticleEntity]>> {
        articlesStream
    }

    // MARK: - getArticles(query:page:refresh:)

    func getArticles(query: String, page: Int, refresh: Bool) -> AsyncStream<Resource<[ArticleEntity]>> {
        articlesRefreshStream
    }
}
