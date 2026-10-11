//
//  QueryEntity.swift
//  news
//
//  Created by Mendez, Juan on 11/7/25.
//

import Foundation
import SwiftData

@Model
class QueryModel {
    @Attribute(.unique)
    var queryName: String
    @Relationship(deleteRule: .nullify, inverse: \ArticleModel.queries)
    var articles: [ArticleModel] = []

    init(queryName: String, articles: [ArticleModel]) {
        self.queryName = queryName
        self.articles = articles
    }
}
