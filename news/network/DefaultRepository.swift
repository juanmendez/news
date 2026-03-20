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
            loadFromCache: { @Sendable () async -> [ArticleEntity] in
                return await database.readArticles(query)
            },
            shouldFetchFromNetwork: { @Sendable (data: [ArticleEntity]?) -> Bool in
                if refresh {
                    return true
                } else {
                    return page > 1 || (data == nil || data?.isEmpty == true)
                }
            },
            fetchFromNetwork: { @Sendable () async throws -> [ArticleEntity] in
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

                let mapper = ArticleEntityMapper()
                return response.model.articles.map(mapper.toEntity)
            },
            saveToCache: { @Sendable (articles: [ArticleEntity]) async throws -> Void in
                for articleEntity in articles {
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
