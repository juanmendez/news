//
//  SessionNewsDatabase.swift
//  news
//
//  Created by Mendez, Juan on 11/6/25.
//

import Foundation
import GRDB

struct SessionNewsDatabase: NewsDatabase {
    private static var articles: [ArticleEntity] = []

    func saveArticle(_ articleEntity: ArticleEntity) {
        Self.articles.append(articleEntity)
    }
    
    func readArticles() -> [ArticleEntity] {
        Self.articles
    }
    
    static func setupConfiguration(_ configuration: inout GRDB.Configuration) {

    }
}
