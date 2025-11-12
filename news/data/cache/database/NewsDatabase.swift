//
//  NewsDatabase.swift
//  news
//
//  Created by Mendez, Juan on 11/4/25.
//

import Foundation
import GRDB

protocol NewsDatabase: Sendable {
    func saveArticle(_ query: String, articleEntity: ArticleEntity)
    func readArticles(_ query: String) -> [ArticleEntity]
    static func setupConfiguration(_ configuration: inout Configuration)
}
