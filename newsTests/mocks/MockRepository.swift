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
@MainActor
final class MockRepository: Repository {

    // MARK: - Stub values

    /// Assign an array of values to yield for getArticles(query:page:)
    var articlesStreamValues: [Resource<[ArticleEntity]>] = []

    /// Assign an array of values to yield for getArticles(query:page:refresh:)
    var articlesRefreshStreamValues: [Resource<[ArticleEntity]>] = []

    /// Assign an array of Resource values to yield for getQueries()
    var queriesStreamValues: [Resource<[QueryEntity]>] = []

    // MARK: - getArticles(query:page:)

    func getArticles(
        query: String,
        page: Int,
        pageSize: Int
    ) -> AsyncStream<Resource<[ArticleEntity]>> {
        AsyncStream { continuation in
            for value in articlesStreamValues {
                continuation.yield(value)
            }
            continuation.finish()
        }
    }

    // MARK: - getArticles(query:page:refresh:)

    func getArticles(
        query: String,
        page: Int,
        pageSize: Int,
        refresh: Bool
    ) -> AsyncStream<Resource<[ArticleEntity]>> {
        AsyncStream { continuation in
            for value in articlesRefreshStreamValues {
                continuation.yield(value)
            }
            continuation.finish()
        }
    }

    // MARK: - getQueries()
    func getQueries() -> AsyncStream<Resource<[QueryEntity]>> {
        AsyncStream { continuation in
            for value in queriesStreamValues {
                continuation.yield(value)
            }
            continuation.finish()
        }
    }

    // MARK: - deleteArticles(query:)

    func deleteArticles(query: String) async throws { }
}
