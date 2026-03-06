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
    var database: NewsDatabase

    func getArticles(query: String, page: Int, pageSize: Int, refresh: Bool) -> AsyncStream<Resource<[ArticleEntity]>> {
        return ResourceProvider.networkBoundResource(
            loadFromCache: {
                // Load from cache implementation
                database.readArticles(query)
            },
            shouldFetchFromNetwork: { data in
                // Determine if we should fetch from network
                if refresh {
                    true
                } else {
                    page > 1 || (data == nil || data?.isEmpty == true)
                }
            },
            fetchFromNetwork: {
                let response: HttpClientResponse<ArticlesResponse> = try await httpClient.request(
                    router: DefaultHttpRouter.newsByPage,
                    headers: nil,
                    queryItems: [
                        URLQueryItem(name: "q", value: query),
                        URLQueryItem(name: "page", value: String(page)),
                        URLQueryItem(name: "pageSize", value: String(pageSize)),
                        URLQueryItem(name: "sortBy", value: "publishedAt"),
                        URLQueryItem(name: "language", value: "en"),
                        URLQueryItem(name: "apiKey", value: apiKey),
                    ],
                    body: nil
                )

                return response.model.articles
            },
            saveToCache: { articles in
                let mapper = ArticleEntityMapper()
                let articlesEntity = articles.map(mapper.toEntity)
                for articleEntity in articlesEntity {
                    try await database.saveArticle(query, articleEntity: articleEntity)
                }
            }

        )
    }

    func getArticles(query: String, page: Int, pageSize: Int) -> AsyncStream<Resource<[ArticleEntity]>> {
        self.getArticles(query: query, page: page, pageSize: pageSize, refresh: false)
    }

    func deleteArticles(query: String) async throws {
        try await database.deleteArticles(query)
    }

}
