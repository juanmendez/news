//
//  ArticlesResponse.swift
//  news
//
//  Created by Mendez, Juan on 10/13/25.
//

import Foundation

struct ArticlesResponse: nonisolated Codable, Equatable, Sendable {
    let status: String
    let totalResults: Int
    let articles: [Article]

    nonisolated init( status: String, totalResults: Int, articles: [Article]) {
        self.status = status
        self.totalResults = totalResults
        self.articles = articles
    }

    nonisolated init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        self.status = try container.decodeIfPresent(String.self, forKey: .status) ?? ""
        self.totalResults = try container.decodeIfPresent(Int.self, forKey: .totalResults) ?? 0
        self.articles = try container.decodeIfPresent([Article].self, forKey: .articles) ?? []
    }
}
