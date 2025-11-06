//
//  ArticleEntity.swift
//  news
//
//  Created by Mendez, Juan on 10/14/25.
//

import Foundation
import GRDB

struct ArticleEntity: Codable, Equatable, FetchableRecord, PersistableRecord  {
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


    enum Columns {
        static let id = Column(CodingKeys.id)
        static let sourceId = Column(CodingKeys.sourceId)
        static let sourceName = Column(CodingKeys.sourceName)
        static let author = Column(CodingKeys.author)
        static let title = Column(CodingKeys.title)
        static let description = Column(CodingKeys.description)
        static let url = Column(CodingKeys.url)
        static let imageUrl = Column(CodingKeys.imageUrl)
        static let publishedAt = Column(CodingKeys.publishedAt)
        static let content = Column(CodingKeys.content)
    }
}
