//
//  newsTests.swift
//  newsTests
//
//  Created by Mendez, Juan on 9/16/25.
//

import Mockingbird
import Testing

@testable import news

struct NewsTests {

    // Pods/MockingbirdFramework/mockingbird configure newsTests -- --targets news

    // Helper function to create test ArticleEntity with all required fields
    private func makeArticle(
        id: String,
        sourceId: String? = nil,
        sourceName: String = "Test Source",
        author: String = "Test Author",
        title: String,
        description: String = "Test description",
        url: String,
        imageUrl: String = "https://test.com/image.jpg",
        publishedAt: Int64 = 1_700_000_000_000,
        content: String = "Test content"
    ) -> ArticleEntity {
        ArticleEntity(
            id: id,
            sourceId: sourceId,
            sourceName: sourceName,
            author: author,
            title: title,
            description: description,
            url: url,
            imageUrl: imageUrl,
            publishedAt: publishedAt,
            content: content
        )
    }

    @Test func responseHasNoArticlesVerifyThereAreNoArticles() async throws {
        let repository = mock(Repository.self)

        let asynStream = AsyncStream<Resource<[ArticleEntity]>> { continuation in
            continuation.yield(Resource.loading())
            continuation.yield(Resource.success(item: []))
            continuation.finish()
        }

        given(repository.getArticles(query: any(), page: any())).willReturn(asynStream)
        let result = await repository.getArticles(query: "", page: 1).collect()

        #expect(result.first == Resource.loading())
        #expect(result.last == Resource.success(item: []))
    }

    @Test func responseComesWithError() async throws {
        let repository = mock(Repository.self)

        let asynStream = AsyncStream<Resource<[ArticleEntity]>> { continuation in
            continuation.yield(Resource.loading())
            continuation.yield(Resource.error(error: HttpError.invalidUrl))
            continuation.finish()
        }

        given(repository.getArticles(query: any(), page: any())).willReturn(asynStream)
        let result = await repository.getArticles(query: "", page: 1).collect()

        #expect(result.first == Resource.loading())
        #expect(result.last == Resource.error(error: HttpError.invalidUrl))
    }

    @Test func loadingWithPartialDataThenSuccess() async throws {
        let repository = mock(Repository.self)
        let partialArticles = [
            makeArticle(id: "1", title: "Loading Article", url: "http://test.com")
        ]
        let fullArticles = [
            makeArticle(
                id: "1",
                sourceName: "CNN",
                title: "Article 1",
                description: "Desc 1",
                url: "http://test.com/1",
                publishedAt: 1_700_000_000_000
            ),
            makeArticle(
                id: "2",
                sourceName: "BBC",
                title: "Article 2",
                description: "Desc 2",
                url: "http://test.com/2",
                publishedAt: 1_700_086_400_000
            ),
        ]

        let stream = AsyncStream<Resource<[ArticleEntity]>> { continuation in
            continuation.yield(Resource.loading(item: partialArticles))
            continuation.yield(Resource.success(item: fullArticles))
            continuation.finish()
        }

        given(repository.getArticles(query: any(), page: any())).willReturn(stream)
        let result = await repository.getArticles(query: "Top Headlines", page: 1).collect()

        #expect(result.count == 2)
        #expect(result.first == Resource.loading(item: partialArticles))
        #expect(result.last == Resource.success(item: fullArticles))
    }

    @Test func multipleLoadingStatesThenSuccess() async throws {
        let repository = mock(Repository.self)
        let articles = [
            makeArticle(id: "1", title: "Test Article", url: "http://test.com")
        ]

        let stream = AsyncStream<Resource<[ArticleEntity]>> { continuation in
            continuation.yield(Resource.loading())
            continuation.yield(Resource.loading(item: []))
            continuation.yield(Resource.loading(item: articles))
            continuation.yield(Resource.success(item: articles))
            continuation.finish()
        }

        given(repository.getArticles(query: any(), page: any())).willReturn(stream)
        let result = await repository.getArticles(query: "Sports", page: 1).collect()

        #expect(result.count == 4)
        #expect(result[0] == Resource.loading())
        #expect(result[1] == Resource.loading(item: []))
        #expect(result[2] == Resource.loading(item: articles))
        #expect(result[3] == Resource.success(item: articles))
    }

    @Test func errorAfterLoadingWithNoSuccessState() async throws {
        let repository = mock(Repository.self)
        let error = HttpError.badResponse(status: 500, error: nil, result: nil)

        let stream = AsyncStream<Resource<[ArticleEntity]>> { continuation in
            continuation.yield(Resource.loading())
            continuation.yield(Resource.loading(item: []))
            continuation.yield(Resource.error(error: error))
            continuation.finish()
        }

        given(repository.getArticles(query: any(), page: any())).willReturn(stream)
        let result = await repository.getArticles(query: "Breaking News", page: 1).collect()

        #expect(result.count == 3)
        #expect(result[0] == Resource.loading())
        #expect(result[1] == Resource.loading(item: []))
        #expect(result[2] == Resource.error(error: error))
    }

    @Test func emptyStreamFinishesImmediately() async throws {
        let repository = mock(Repository.self)

        let stream = AsyncStream<Resource<[ArticleEntity]>> { continuation in
            continuation.finish()
        }

        given(repository.getArticles(query: any(), page: any())).willReturn(stream)
        let result = await repository.getArticles(query: "", page: 1).collect()

        #expect(result.isEmpty)
    }

    @Test func multipleSuccessivePages() async throws {
        let repository = mock(Repository.self)
        let page1Articles = [
            makeArticle(id: "1", title: "Article 1", url: "http://test.com/1", publishedAt: 1_700_000_000_000)
        ]
        let page2Articles = [
            makeArticle(id: "2", title: "Article 2", url: "http://test.com/2", publishedAt: 1_700_086_400_000)
        ]

        let stream1 = AsyncStream<Resource<[ArticleEntity]>> { continuation in
            continuation.yield(Resource.loading())
            continuation.yield(Resource.success(item: page1Articles))
            continuation.finish()
        }

        let stream2 = AsyncStream<Resource<[ArticleEntity]>> { continuation in
            continuation.yield(Resource.loading())
            continuation.yield(Resource.success(item: page2Articles))
            continuation.finish()
        }

        given(repository.getArticles(query: any(), page: 1)).willReturn(stream1)
        given(repository.getArticles(query: any(), page: 2)).willReturn(stream2)

        let result1 = await repository.getArticles(query: "Tech", page: 1).collect()
        let result2 = await repository.getArticles(query: "Tech", page: 2).collect()

        #expect(result1.last == Resource.success(item: page1Articles))
        #expect(result2.last == Resource.success(item: page2Articles))
    }

    @Test func differentErrorTypes() async throws {
        let repository = mock(Repository.self)
        let errors: [Error] = [
            HttpError.invalidUrl,
            HttpError.badResponse(status: 404, error: nil, result: nil),
        ]

        for (index, error) in errors.enumerated() {
            let stream = AsyncStream<Resource<[ArticleEntity]>> { continuation in
                continuation.yield(Resource.loading())
                continuation.yield(Resource.error(error: error))
                continuation.finish()
            }

            given(repository.getArticles(query: "test\(index)", page: any())).willReturn(stream)
            let result = await repository.getArticles(query: "test\(index)", page: 1).collect()

            #expect(result.last == Resource.error(error: error))
        }
    }

    @Test func successWithLargeArticleList() async throws {
        let repository = mock(Repository.self)
        let largeList = (1...100).map { id in
            makeArticle(
                id: "\(id)",
                sourceName: "Source \(id)",
                author: "Author \(id)",
                title: "Article \(id)",
                description: "Description \(id)",
                url: "http://test.com/\(id)",
                imageUrl: "http://test.com/image\(id).jpg",
                publishedAt: Int64(1_700_000_000_000 + (id * 86_400_000)),
                content: "Content \(id)"
            )
        }

        let stream = AsyncStream<Resource<[ArticleEntity]>> { continuation in
            continuation.yield(Resource.loading())
            continuation.yield(Resource.success(item: largeList))
            continuation.finish()
        }

        given(repository.getArticles(query: any(), page: any())).willReturn(stream)
        let result = await repository.getArticles(query: "All News", page: 1).collect()

        if case .success(let articles) = result.last {
            #expect(articles.count == 100)
            #expect(articles.first?.id == "1")
            #expect(articles.last?.id == "100")
            #expect(articles.first?.title == "Article 1")
            #expect(articles.last?.author == "Author 100")
        } else {
            Issue.record("Expected success with 100 articles")
        }
    }

    @Test func articleEntityEquality() async throws {
        let article1 = makeArticle(id: "1", title: "Same Article", url: "http://test.com")
        let article2 = makeArticle(id: "1", title: "Same Article", url: "http://test.com")
        let article3 = makeArticle(id: "2", title: "Different Article", url: "http://test.com/2")

        #expect(article1 == article2)
        #expect(article1 != article3)
    }

    @Test func articlesWithDifferentSourcesAreNotEqual() async throws {
        let article1 = makeArticle(id: "1", sourceId: "cnn", sourceName: "CNN", title: "News", url: "http://test.com")
        let article2 = makeArticle(id: "1", sourceId: "bbc", sourceName: "BBC", title: "News", url: "http://test.com")

        #expect(article1 != article2)
    }

    @Test func articlesWithDifferentPublishDatesAreNotEqual() async throws {
        let article1 = makeArticle(id: "1", title: "News", url: "http://test.com", publishedAt: 1_700_000_000_000)
        let article2 = makeArticle(id: "1", title: "News", url: "http://test.com", publishedAt: 1_700_086_400_000)

        #expect(article1 != article2)
    }

    @Test func successStreamWithArticlesContainingAllFields() async throws {
        let repository = mock(Repository.self)
        let articles = [
            makeArticle(
                id: "tech-1",
                sourceId: "techcrunch",
                sourceName: "TechCrunch",
                author: "Sarah Johnson",
                title: "New AI Breakthrough",
                description: "Scientists announce major advancement in artificial intelligence",
                url: "https://techcrunch.com/ai-breakthrough",
                imageUrl: "https://techcrunch.com/images/ai.jpg",
                publishedAt: 1_700_000_000_000,
                content: "Full article content about AI breakthrough..."
            ),
            makeArticle(
                id: "tech-2",
                sourceId: "wired",
                sourceName: "Wired",
                author: "John Smith",
                title: "Quantum Computing Update",
                description: "Latest developments in quantum computing field",
                url: "https://wired.com/quantum-update",
                imageUrl: "https://wired.com/images/quantum.jpg",
                publishedAt: 1_700_086_400_000,
                content: "Full article content about quantum computing..."
            ),
        ]

        let stream = AsyncStream<Resource<[ArticleEntity]>> { continuation in
            continuation.yield(Resource.loading())
            continuation.yield(Resource.success(item: articles))
            continuation.finish()
        }

        given(repository.getArticles(query: any(), page: any())).willReturn(stream)
        let result = await repository.getArticles(query: "Technology", page: 1).collect()

        if case .success(let returnedArticles) = result.last {
            #expect(returnedArticles.count == 2)
            #expect(returnedArticles[0].sourceId == "techcrunch")
            #expect(returnedArticles[0].author == "Sarah Johnson")
            #expect(returnedArticles[1].sourceName == "Wired")
            #expect(returnedArticles[1].publishedAt == 1_700_086_400_000)
        } else {
            Issue.record("Expected success with 2 articles")
        }
    }

    @Test func articlesWithOptionalSourceIdCanBeNil() async throws {
        let repository = mock(Repository.self)
        let articlesWithNilSourceId = [
            makeArticle(id: "1", sourceId: nil, title: "Article without source ID", url: "http://test.com")
        ]

        let stream = AsyncStream<Resource<[ArticleEntity]>> { continuation in
            continuation.yield(Resource.success(item: articlesWithNilSourceId))
            continuation.finish()
        }

        given(repository.getArticles(query: any(), page: any())).willReturn(stream)
        let result = await repository.getArticles(query: "", page: 1).collect()

        if case .success(let articles) = result.last {
            #expect(articles.first?.sourceId == nil)
            #expect(articles.first?.sourceName == "Test Source")
        } else {
            Issue.record("Expected success")
        }
    }

    @Test func sortArticlesByPublishedDate() async throws {
        let repository = mock(Repository.self)
        let articles = [
            makeArticle(id: "3", title: "Newest", url: "http://test.com/3", publishedAt: 1_700_172_800_000),
            makeArticle(id: "1", title: "Oldest", url: "http://test.com/1", publishedAt: 1_700_000_000_000),
            makeArticle(id: "2", title: "Middle", url: "http://test.com/2", publishedAt: 1_700_086_400_000),
        ]

        let stream = AsyncStream<Resource<[ArticleEntity]>> { continuation in
            continuation.yield(Resource.success(item: articles))
            continuation.finish()
        }

        given(repository.getArticles(query: any(), page: any())).willReturn(stream)
        let result = await repository.getArticles(query: "", page: 1).collect()

        if case .success(let returnedArticles) = result.last {
            let sorted = returnedArticles.sorted { $0.publishedAt > $1.publishedAt }
            #expect(sorted[0].title == "Newest")
            #expect(sorted[1].title == "Middle")
            #expect(sorted[2].title == "Oldest")
        } else {
            Issue.record("Expected success")
        }
    }
}
