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
public struct Article: nonisolated Codable, nonisolated Equatable, Sendable {
    let id: String
    let source: Source
    let author: String
    let title: String
    let description: String
    let url: String
    let urlToImage: String
    let publishedAt: Date
    let content: String

    nonisolated init(
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

    nonisolated public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        let source = try container.decodeIfPresent(Source.self, forKey: .source) ?? Source(id: nil, name: "")
        let author = try container.decodeIfPresent(String.self, forKey: .author) ?? ""
        let title = try container.decodeIfPresent(String.self, forKey: .title) ?? ""
        let description = try container.decodeIfPresent(String.self, forKey: .description) ?? ""
        let url = try container.decodeIfPresent(String.self, forKey: .url) ?? ""
        let urlToImage = try container.decodeIfPresent(String.self, forKey: .urlToImage) ?? ""
        let publishedAt = try container.decodeIfPresent(Date.self, forKey: .publishedAt) ?? Date()
        let content = try container.decodeIfPresent(String.self, forKey: .content) ?? ""

        // Compute id deterministically: prefer title, then url, otherwise random UUID
        let id: String
        if !title.isEmpty {
            id = title
        } else if !url.isEmpty {
            id = url
        } else {
            id = UUID().uuidString
        }

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
}

struct Source: nonisolated Codable, nonisolated Equatable, Sendable {
    let id: String?
    let name: String

    nonisolated init( id: String?, name: String) {
        self.id = id
        self.name = name
    }

    nonisolated init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        self.id = try container.decodeIfPresent(String.self, forKey: .id)
        self.name = try container.decodeIfPresent(String.self, forKey: .name) ?? ""
    }
}
