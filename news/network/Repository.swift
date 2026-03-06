//
//  Repository.swift
//  news
//
//  Created by Mendez, Juan on 10/14/25.
//

import Foundation

protocol Repository {
    /**
     * Retrieves a list of [Article] matching a given [query] and [page]
     * @param query the matching query
     * @param page the matching page
     * @return the [Flow] of [Resource] of the list of matching [Article]
     */
    func getArticles(
        query: String,
        page: Int,
        pageSize: Int,
    ) -> AsyncStream<Resource<[ArticleEntity]>>

    /**
     * Retrieves a list of [Article] matching a given [query] and [page]
     * @param query the matching query
     * @param page the matching page
     * @param pageSize the number of articles per page
     * @return the [Flow] of [Resource] of the list of matching [Article]
     */
    func getArticles(
        query: String,
        page: Int,
        pageSize: Int,
        refresh: Bool,
    ) -> AsyncStream<Resource<[ArticleEntity]>>

    /**
     * Retrieves a list of [Article] matching "Top Headlines" and [page]
     * @param page the matching page
     * @return the [Flow] of [Resource] of the list of matching [Article]
     */
    //func getTopHeadlines(page: Int) -> AsyncStream<Resource<ArticlesResponse>>

    /**
     * Deletes all articles matching a given [query]
     * @param query the matching query
     */
    func deleteArticles(query: String) async throws

    /**
     * Deletes all articles
     */
    //func deleteAllArticles() -> AsyncStream<Resource<NoResponse>>
}
