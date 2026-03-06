//
//  NewsDatabase.swift
//  news
//
//  Created by Mendez, Juan on 11/4/25.
//

import Foundation
import GRDB

protocol NewsDatabase: Sendable {
    func saveArticle(_ query: String, articleEntity: ArticleEntity) async throws
    func readArticles(_ query: String) -> [ArticleEntity]
    func deleteArticles(_ query: String) async throws
    static func setupConfiguration(_ configuration: inout Configuration)
}
