//
//  QuriesViewModelTests.swift
//  newsTests
//
//  Created by Mendez, Juan on 4/17/26.
//

import Foundation
import Testing
@testable import news

@MainActor
struct QueriesViewModelTests {
    let httpClient = MockHttpClient()
    let database: MockNewsDatabase
    let repository: DefaultRepository
    let sut: QueriesViewModel

    init() throws {
        database = try MockNewsDatabase()
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
        try await database.preload(TOP_HEADLINES, articles: cachedEntities)
        // when
        // Simulate whatever triggers the queries to be loaded, e.g. refreshQueries()
        await sut.refreshQueries()
        // then
        let expectedQueries = [QueryEntity(queryName: TOP_HEADLINES)]
        #expect(sut.queries == expectedQueries)
    }
}
