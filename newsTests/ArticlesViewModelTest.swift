//
//  ArticlesViewModelTest.swift
//  newsTests
//
//  Created by Mendez, Juan on 3/3/26.
//

import Testing

@testable import news

@MainActor
struct ArticlesViewModelTest {

    let httpClient = MockHttpClient()
    let internetService = MockInternetService()
    let database = MockNewsDatabase()
    let pageSize = 2

    var repository: DefaultRepository {
        DefaultRepository(
            httpClient: httpClient,
            apiKey: NewsApi.key,
            database: database
        )
    }

    func makeArticlesResponse(_ articles: [Article]) throws -> HttpClientResponseRaw {
        try HttpClientResponseRaw(item: ArticlesResponse(
            status: "ok",
            totalResults: PreviewConstants.articles.count,
            articles: articles
        ))
    }

    @Test func initialStateIsEmpty() {
        // given
        let sut = ArticlesViewModel(repository: repository, internetService: internetService, pageSize: pageSize)

        // then
        #expect(sut.articles.isEmpty)
        #expect(sut.showProgress == false)
        #expect(sut.errorMessage == nil)
        #expect(sut.isScrollingFinished == false)
        #expect(sut.query == "Top Headlines")
    }

    @Test func fetchFirstPageLoadsArticlesFromNetwork() async throws {
        // given
        let pageArticles0 = PreviewConstants.articles.slice(0, pageSize)

        let sut = ArticlesViewModel(repository: repository, internetService: internetService, pageSize: pageSize)

        // when
        httpClient.rawResponse = try makeArticlesResponse(pageArticles0)
        await sut.fetchArticles()

        // then
        #expect(sut.articles.count == pageSize)
        #expect(sut.isScrollingFinished == false)
    }

    @Test func fetchingTwiceAppendsPagesOfArticles() async throws {
        // given
        let pageArticles0 = PreviewConstants.articles.slice(0, pageSize)
        let pageArticles1 = PreviewConstants.articles.slice(pageSize, pageSize)

        let sut = ArticlesViewModel(repository: repository, internetService: internetService, pageSize: pageSize)

        // when
        httpClient.rawResponse = try makeArticlesResponse(pageArticles0)
        await sut.fetchArticles()

        httpClient.rawResponse = try makeArticlesResponse(pageArticles1)
        await sut.fetchArticles()

        // then
        #expect(sut.articles.count == pageSize * 2)
        #expect(sut.isScrollingFinished == false)
    }

    @Test func fetchingThreeTimesFinishesPagination() async throws {
        // given
        let pageArticles0 = PreviewConstants.articles.slice(0, pageSize)
        let pageArticles1 = PreviewConstants.articles.slice(pageSize, pageSize)
        let pageArticles2: [Article] = []

        let sut = ArticlesViewModel(repository: repository, internetService: internetService, pageSize: pageSize)

        // when
        httpClient.rawResponse = try makeArticlesResponse(pageArticles0)
        await sut.fetchArticles()

        httpClient.rawResponse = try makeArticlesResponse(pageArticles1)
        await sut.fetchArticles()

        httpClient.rawResponse = try makeArticlesResponse(pageArticles2)
        await sut.fetchArticles()

        // then
        #expect(sut.articles.count == pageSize * 2)
        #expect(sut.isScrollingFinished == true)
    }

    @Test func fetchFirstPageStoresArticlesInDatabase() async throws {
        // given
        let pageArticles0 = PreviewConstants.articles.slice(0, pageSize)

        let sut = ArticlesViewModel(repository: repository, internetService: internetService, pageSize: pageSize)

        // when
        httpClient.rawResponse = try makeArticlesResponse(pageArticles0)
        await sut.fetchArticles()

        // then
        let storedArticles = database.mapQueryArticles[sut.query]
        #expect(storedArticles?.count == pageSize)
        #expect(sut.isScrollingFinished == false)
    }

    @Test func fetchArticlesLoadsFromCacheWhenAvailable() async throws {
        // given
        let cachedEntities = PreviewConstants.articleEntities.slice(0, pageSize)
        let preloadedDatabase = MockNewsDatabase(mapQueryArticles: ["Top Headlines": cachedEntities])

        let cachedRepository = DefaultRepository(
            httpClient: httpClient,
            apiKey: NewsApi.key,
            database: preloadedDatabase
        )

        let sut = ArticlesViewModel(repository: cachedRepository, internetService: internetService, pageSize: pageSize)

        // when
        await sut.fetchArticles()

        // then
        #expect(sut.articles.count == pageSize)
        #expect(sut.articles == cachedEntities)
        #expect(sut.isScrollingFinished == true)
    }


}
