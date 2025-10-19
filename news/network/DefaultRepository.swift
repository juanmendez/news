//
//  DefaultRepository.swift
//  News
//
//  Created by Mendez, Juan on 10/18/25.
//

import Foundation

struct DefaultRepository: Repository {
    let httpClient: HttpClient
    let apiKey: String

    func getArticles(query: String, page: Int) -> AsyncStream<Resource<[ArticleEntity]>> {
        return ResourceProvider.networkBoundResource(
            loadFromCache: {
                // Load from cache implementation
                ArticlesResponseCacheEnum.articleEntities
            },
            shouldFetchFromNetwork: { data in
                // Determine if we should fetch from network
                data == nil || data?.isEmpty == true
            },
            fetchFromNetwork: {
                let response: HttpClientResponse<ArticlesResponse> = try await httpClient.request(
                    router: DefaultHttpRouter.newsByPage,
                    headers: nil,
                    queryItems: [
                        URLQueryItem(name: "q", value: "Top Headlines"),
                        URLQueryItem(name: "page", value: "1"),
                        URLQueryItem(name: "pageSize", value: "10"),
                        URLQueryItem(name: "sortBy", value: "publishedAt"),
                        URLQueryItem(name: "language", value: "en"),
                        URLQueryItem(name: "apiKey", value: apiKey),
                    ],
                    body: nil
                )

                return response.model.articles
            },
            saveToCache: { data in
                let mapper = ArticleEntityMapper()
                ArticlesResponseCacheEnum.articleEntities += data.map(mapper.toEntity)
            }

        )
    }
}
