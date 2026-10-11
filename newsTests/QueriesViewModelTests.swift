//
//  QuriesViewModelTests.swift
//  newsTests
//
//  Created by Mendez, Juan on 4/17/26.
//

import Foundation
import SwiftData
import Testing
@testable import news

@MainActor
struct QueriesViewModelTests {
    let httpClient = MockHttpClient()
    let database: NewsDatabase
    let repository: DefaultRepository
    let sut: QueriesViewModel

    init() throws {
        let container = try ModelContainer(
            for: QueryModel.self, ArticleModel.self,
            configurations: ModelConfiguration(isStoredInMemoryOnly: true)
        )
        database = DefaultNewsDatabase(modelContext: ModelContext(container))

        repository = DefaultRepository(
            httpClient: httpClient,
            apiKey: NewsApi.key,
            database: database
        )
        sut = QueriesViewModel(repository: repository)
    }

    @Test func queriesReflectPreloadedCache() async throws {
        // given
        let cachedEntities = PreviewConstants.articleEntities.slice(0, 2)
        try preload(TOP_HEADLINES, articles: cachedEntities)
        // when
        // Simulate whatever triggers the queries to be loaded, e.g. refreshQueries()
        await sut.refreshQueries()
        // then
        let expectedQueries = [QueryEntity(queryName: TOP_HEADLINES)]
        #expect(sut.queries == expectedQueries)
    }

    @Test func whenTappingToDeleteThenQueryIsDeleted() async throws {
        // given
        let initialArticleEntities = PreviewConstants.articleEntities.slice(0, 2)
        try preload(TOP_HEADLINES, articles: initialArticleEntities)

        // when
        await sut.refreshQueries()
        let originalQueries = sut.queries
        let deleteFirstQuery = originalQueries[0]
        await sut.deleteQueries([deleteFirstQuery])

        // then
        await sut.refreshQueries()
        #expect(!sut.queries.contains(deleteFirstQuery))
    }

    private func preload(_ query: String, articles: [ArticleEntity]) throws {
        for articleEntity in articles {
            try database.saveArticle(query, articleEntity: articleEntity)
        }
    }
}
