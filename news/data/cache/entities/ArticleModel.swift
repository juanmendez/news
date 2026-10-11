//
//  ArticleEntity.swift
//  news
//
//  Created by Mendez, Juan on 10/14/25.
//

import Foundation
import SwiftData

@Model
class ArticleModel: Identifiable {
    @Attribute(.unique)
    var id: String
    var sourceId: String?
    var sourceName: String
    var author: String
    var title: String
    var about: String
    var url: String
    var imageUrl: String
    var publishedAt: Int64
    var content: String
    var queries: [QueryModel]

    init(
        id: String,
        sourceId: String?,
        sourceName: String,
        author: String,
        title: String,
        about: String,
        url: String,
        imageUrl: String,
        publishedAt: Int64,
        content: String,
        queries: [QueryModel]
    ) {
        self.id = id
        self.sourceId = sourceId
        self.sourceName = sourceName
        self.author = author
        self.title = title
        self.about = about
        self.url = url
        self.imageUrl = imageUrl
        self.publishedAt = publishedAt
        self.content = content
        self.queries = queries
    }
}
