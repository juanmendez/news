//
//  ArticleEntity.swift
//  news
//
//  Created by Mendez, Juan on 10/14/25.
//

import Foundation

nonisolated
struct ArticleEntity: Codable, Equatable, Hashable, Sendable {
    let id: String // PrimaryKey
    let sourceId: String?
    let sourceName: String
    let author: String
    let title: String
    let description: String
    let url: String
    let imageUrl: String
    let publishedAt: Int64
    let content: String
}
