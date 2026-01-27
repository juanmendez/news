//
//  ArticlesViewModelTest.swift
//  newsTests
//
//  Created by Mendez, Juan on 11/12/25.
//

import Mockingbird
import Testing

@testable import news

struct HttpClientTest {
    @Test func findOutHowToMockAJsonForHttpClientResponse() async throws {
        let json = """
            {
            "source": {
            "id": "the-verge",
            "name": "The Verge"
            },
            "author": "Aliana Alexandra Coello",
            "title": "Murmurations: Puerto Rico’s Resilient History Mirrors the Mangrove",
            "description": "description",
            "url": "https://www.yesmagazine.org/environmental-justice/2025/05/15/murmurations-puerto-rico-mangroves",
            "urlToImage": "https://i0.wp.com/www.yesmagazine.org/wp-content/uploads/2025/05/Mangroves_1400x840.jpg",
            "publishedAt": "2025-05-15T16:32:17Z",
            "content": "There is a popular saying among organizers across movements: They wanted to bury us, but they didn’t know we were seeds."
            }
            """

        let rawResponse = HttpClientResponseRaw(
            (
                data: json.data(using: .utf8) ?? Data(),
                response: HTTPURLResponse.init(
                    url: URL(string: "https://newsapi.org")!,
                    statusCode: 200,
                    httpVersion: nil,
                    headerFields: [:]
                )!
            )
        )

        let sut = mock(HttpClient.self)

        await given(
            sut.rawRequest(router: any(), headers: any(), queryItems: any(), body: any())
        ).willReturn(rawResponse)

        let response: HttpClientResponse<Article> = try await sut.request(
            router: DefaultHttpRouter.newsByPage,
            headers: nil,
            queryItems: [],
            body: nil
        )

        let article = response.model

        #expect(article.author == "Aliana Alexandra Coello")
        #expect(article.title == "Murmurations: Puerto Rico’s Resilient History Mirrors the Mangrove")
    }

}
