//
//  ArticlesViewModelTest.swift
//  newsTests
//
//  Created by Mendez, Juan on 3/3/26.
//

import Foundation
import Testing
@testable import news

@MainActor
struct ArticlesViewModelTest {
    
    let httpClient = MockHttpClient()
    let internetService = MockInternetService()
    let database: MockNewsDatabase
    let pageSize = 2
    
    init() throws {
        database = try MockNewsDatabase()
    }
    
    var repository: DefaultRepository {
        DefaultRepository(
            httpClient: httpClient,
            apiKey: NewsApi.key,
            database: database
        )
    }
    
    func makeArticlesResponse(_ articles: [Article]) throws -> HttpClientResponseRaw {
        try HttpClientResponseRaw(
            item: ArticlesResponse(
                status: "ok",
                totalResults: PreviewConstants.articles.count,
                articles: articles
            )
        )
    }
    
    // MARK: - Initial State
    
    @Test func initialStateIsEmpty() {
        // given
        let sut = ArticlesViewModel(repository: repository, internetService: internetService, pageSize: pageSize)
        
        // then
        #expect(sut.articles.isEmpty)
        #expect(sut.errorMessage == nil)
        #expect(sut.isScrollingFinished == false)
        #expect(sut.query == TOP_HEADLINES)
    }
    
    // MARK: - Fetch Articles
    
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
        #expect(sut.scrollToTop == false)
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
    
    @Test func fetchArticlesShowsErrorWhenOfflineAndNoCache() async throws {
        // given
        let sut = ArticlesViewModel(repository: repository, internetService: internetService, pageSize: pageSize)
        
        // when - no internet, no cached data, no http response
        internetService.access = false
        await sut.fetchArticles()
        
        // then
        #expect(
            sut.errorMessage == String(localized: "something_went_wrong"),
            "No News API Key is found triggers instead if not set"
        )
    }
    
    @Test func fetchFirstPageStoresArticlesInDatabase() async throws {
        // given
        let pageArticles0 = PreviewConstants.articles.slice(0, pageSize)
        
        let sut = ArticlesViewModel(repository: repository, internetService: internetService, pageSize: pageSize)
        
        // when
        httpClient.rawResponse = try makeArticlesResponse(pageArticles0)
        await sut.fetchArticles()
        
        // then
        let storedArticles = await database.readArticles(sut.query)
        #expect(storedArticles.count == pageSize)
        #expect(sut.isScrollingFinished == false)
    }
    
    // MARK: - Refresh Articles
    
    @Test func refreshArticlesClearsPreviousPageAndFetchesFromNetwork() async throws {
        // given
        let pageArticles0 = PreviewConstants.articles.slice(0, pageSize)
        let cachedArticleEntities = pageArticles0.map(ArticleEntityMapper().toEntity)
        
        let sut = ArticlesViewModel(repository: repository, internetService: internetService, pageSize: pageSize)
        
        // when - fetch first page
        httpClient.rawResponse = try makeArticlesResponse(pageArticles0)
        await sut.fetchArticles()
        
        // when - internet is available, refresh
        internetService.access = true
        httpClient.rawResponse = try makeArticlesResponse(pageArticles0)
        await sut.refreshArticles()
        
        // then
        let storedArticles = await database.readArticles(sut.query)
        #expect(storedArticles.sortedById() == cachedArticleEntities.sortedById())
        #expect(sut.articles.sortedById() == cachedArticleEntities.sortedById())
    }
    
    @Test func refreshArticlesDoesNothingWhenQueryIsEmpty() async throws {
        // given
        let sut = ArticlesViewModel(repository: repository, internetService: internetService, pageSize: pageSize)
        
        // when
        sut.query = ""
        await sut.refreshArticles()
        
        // then
        #expect(sut.articles.isEmpty)
        #expect(sut.errorMessage == nil)
        #expect(sut.isScrollingFinished == false)
    }
    
    @Test func refreshArticlesDoesNothingWhenOffline() async throws {
        // given
        let pageArticles0 = PreviewConstants.articles.slice(0, pageSize)
        let cachedArticleEntities = pageArticles0.map(ArticleEntityMapper().toEntity)
        
        let sut = ArticlesViewModel(repository: repository, internetService: internetService, pageSize: pageSize)
        
        // when - fetch first page
        httpClient.rawResponse = try makeArticlesResponse(pageArticles0)
        await sut.fetchArticles()
        
        // when - no internet, attempt refresh
        internetService.access = false
        await sut.refreshArticles()
        
        // then
        #expect(sut.articles.sortedById() == cachedArticleEntities.sortedById())
    }
    
    // MARK: - Submit Articles
    
    @Test func submitArticlesFetchesFromNetwork() async throws {
        // given
        let pageArticles0 = PreviewConstants.articles.slice(0, pageSize)
        let cachedArticleEntities = pageArticles0.map(ArticleEntityMapper().toEntity)
        
        let sut = ArticlesViewModel(repository: repository, internetService: internetService, pageSize: pageSize)
        
        // when
        sut.query = "Bitcoin"
        httpClient.rawResponse = try makeArticlesResponse(pageArticles0)
        await sut.submitArticles()
        
        // then
        #expect(sut.articles.sortedById() == cachedArticleEntities.sortedById())
    }
    
    @Test func submitArticlesClearsPreviousArticles() async throws {
        // given
        let pageArticles0 = PreviewConstants.articles.slice(0, pageSize)
        let pageArticles1 = PreviewConstants.articles.slice(pageSize, pageSize)
        let cachedArticleEntities = pageArticles1.map(ArticleEntityMapper().toEntity)
        
        let sut = ArticlesViewModel(repository: repository, internetService: internetService, pageSize: pageSize)
        
        // when - fetch first page
        httpClient.rawResponse = try makeArticlesResponse(pageArticles0)
        await sut.fetchArticles()
        
        // when - submit resets and fetches page 1 again
        sut.query = "Bitcoin"
        httpClient.rawResponse = try makeArticlesResponse(pageArticles1)
        await sut.submitArticles()
        
        // then
        #expect(sut.articles.count == pageSize)
        #expect(sut.articles.sortedById() == cachedArticleEntities.sortedById())
        
    }
    
    @Test func submitArticlesWithSameQueryResetsToPageOne() async throws {
        // given
        let pageArticles0 = PreviewConstants.articles.slice(0, pageSize)
        let pageArticles1 = PreviewConstants.articles.slice(pageSize, pageSize)
        let cachedArticleEntities = (pageArticles0 + pageArticles1).map(ArticleEntityMapper().toEntity)
        
        let sut = ArticlesViewModel(repository: repository, internetService: internetService, pageSize: pageSize)
        
        // when - fetch two pages
        httpClient.rawResponse = try makeArticlesResponse(pageArticles0)
        await sut.fetchArticles()
        
        httpClient.rawResponse = try makeArticlesResponse(pageArticles1)
        await sut.fetchArticles()
        
        // when - submit same query resets to page 1
        httpClient.rawResponse = try makeArticlesResponse(pageArticles0)
        await sut.submitArticles()
        
        // then - cache has both pages, sut.articles shows page 1 after reset
        #expect(sut.articles.count == cachedArticleEntities.count)
        #expect(sut.articles.sortedById() == cachedArticleEntities.sortedById())
        #expect(sut.isScrollingFinished)
    }
    
    @Test func submitArticlesDoesNothingWhenQueryIsEmpty() async throws {
        // given
        let sut = ArticlesViewModel(repository: repository, internetService: internetService, pageSize: pageSize)
        
        // when
        sut.query = ""
        await sut.submitArticles()
        
        // then
        #expect(sut.articles.isEmpty)
        #expect(sut.errorMessage == nil)
        #expect(sut.isScrollingFinished == false)
    }
    
    // MARK: - Cache
    
    @Test func fetchArticlesLoadsFromCacheWhenAvailable() async throws {
        // given
        let cachedEntities = PreviewConstants.articleEntities.slice(0, pageSize)
        try await database.preload(TOP_HEADLINES, articles: cachedEntities)
        
        let sut = ArticlesViewModel(repository: repository, internetService: internetService, pageSize: pageSize)
        
        // when
        await sut.fetchArticles()
        
        // then
        #expect(sut.articles.count == pageSize)
        #expect(sut.articles.sortedById() == cachedEntities.sortedById())
        #expect(sut.isScrollingFinished)
    }
}
