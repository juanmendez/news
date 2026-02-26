//
//  ArticlesViewModelTest.swift
//  newsTests
//
//  Created by Mendez, Juan on 11/12/25.
//

import Foundation
import Testing

@testable import news

struct HttpClientTest {
    @Test func findOutHowToMockAJsonForHttpClientResponse() async throws {
        let stub = PreviewConstants.articles[0]

        let sut = MockHttpClient()
        sut.rawResponse = try HttpClientResponseRaw(item: stub)

        let response: HttpClientResponse<Article> = try await sut.request(
            router: DefaultHttpRouter.newsByPage,
            headers: nil,
            queryItems: [],
            body: nil
        )

        let article = response.model

        #expect(article.author == stub.author)
        #expect(article.title == stub.title)
    }
}
