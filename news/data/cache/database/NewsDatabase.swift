//
//  NewsDatabase.swift
//  news
//
//  Created by Mendez, Juan on 11/4/25.
//

import Foundation

protocol NewsDatabase: Sendable {
    func saveArticle(_ query: String, articleEntity: ArticleEntity) throws
    func readArticles(_ query: String) -> [ArticleEntity]
    func deleteArticles(_ query: String) throws
    func readQueries() -> [QueryEntity]
}
