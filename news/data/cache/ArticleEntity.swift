//
//  ArticleEntity.swift
//  news
//
//  Created by Mendez, Juan on 10/14/25.
//

import Foundation

struct ArticleEntity: Codable, Equatable {
    var id: UUID // PrimaryKey
    var sourceId: String?
    var sourceName: String
    var author: String
    var title: String
    var description: String
    var url: String
    var imageUrl: String
    var publishedAt: Int64
    var content: String
}
