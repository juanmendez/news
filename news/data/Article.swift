//
//  Article.swift
//  news
//
//  Created by Mendez, Juan on 9/16/25.
//

import Foundation

/// The domain model for storing the article data.
/// This model is used in the Repository and subsequent upper layers.
/// Below the repository we have entity models: a network entity model for the api service, and a
/// cache entity model for the cache service.
public struct Article: Codable, Equatable {
    let id: String
    let query: String
    let sourceId: String
    let sourceName: String
    let author: String
    let title: String
    let description: String
    let url: String
    let imageUrl: String
    let publishedDate: Date
    let content: String

    public init(
        id: String,
        query: String,
        sourceId: String,
        sourceName: String,
        author: String,
        title: String,
        description: String,
        url: String,
        imageUrl: String,
        publishedDate: Date,
        content: String
    ) {
        self.id = id
        self.query = query
        self.sourceId = sourceId
        self.sourceName = sourceName
        self.author = author
        self.title = title
        self.description = description
        self.url = url
        self.imageUrl = imageUrl
        self.publishedDate = publishedDate
        self.content = content
    }
}

