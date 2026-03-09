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
public struct Article: Codable, Equatable, Sendable {
    let id: String
    let source: Source
    let author: String
    let title: String
    let description: String
    let url: String
    let urlToImage: String
    let publishedAt: Date
    let content: String

    init(
        id: String = UUID().uuidString,
        source: Source,
        author: String,
        title: String,
        description: String,
        url: String,
        urlToImage: String,
        publishedAt: Date,
        content: String
    ) {
        self.id = id
        self.source = source
        self.author = author
        self.title = title
        self.description = description
        self.url = url
        self.urlToImage = urlToImage
        self.publishedAt = publishedAt
        self.content = content
    }

    public init(from decoder: any Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        self.source = try container.decodeIfPresent(Source.self, forKey: .source) ?? Source(id: nil, name: "")
        self.author = try container.decodeIfPresent(String.self, forKey: .author) ?? ""
        self.title = try container.decodeIfPresent(String.self, forKey: .title) ?? ""
        self.description = try container.decodeIfPresent(String.self, forKey: .description) ?? ""
        self.url = try container.decodeIfPresent(String.self, forKey: .url) ?? ""
        self.urlToImage = try container.decodeIfPresent(String.self, forKey: .urlToImage) ?? ""
        self.publishedAt = try container.decodeIfPresent(Date.self, forKey: .publishedAt) ?? Date()
        self.content = try container.decodeIfPresent(String.self, forKey: .content) ?? ""

        self.id =  if !self.title.isEmpty {
            self.title
        } else if !self.url.isEmpty {
            self.url
        } else {
            UUID().uuidString
        }
    }
}

struct Source: Codable, Equatable {
    let id: String?
    let name: String
}
