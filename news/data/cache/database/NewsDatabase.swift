//
//  NewsDatabase.swift
//  news
//
//  Created by Mendez, Juan on 11/4/25.
//

import Foundation
import GRDB

protocol NewsDatabase: Sendable {
    func saveArticle(_ articleEntity: ArticleEntity)
    func readArticles() -> [ArticleEntity]
    static func setupConfiguration(_ configuration: inout Configuration)
}
