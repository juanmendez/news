//
//  newsTests.swift
//  newsTests
//
//  Created by Mendez, Juan on 9/16/25.
//

import Testing
import Mockingbird
@testable import news

struct NewsTests {

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
}

