//
//  SessionNewsDatabase.swift
//  news
//
//  Created by Mendez, Juan on 11/6/25.
//

import Foundation
import GRDB

struct SessionNewsDatabase: NewsDatabase {
    private static var articles: [String: [ArticleEntity]] = [:]

    func saveArticle(_ query: String, articleEntity: ArticleEntity) {
        Self.articles[query, default: []].append(articleEntity)
    }

    func readArticles(_ query: String) -> [ArticleEntity] {
        Self.articles[query] ?? []
    }

    static func setupConfiguration(_ configuration: inout GRDB.Configuration) {

    }
}
