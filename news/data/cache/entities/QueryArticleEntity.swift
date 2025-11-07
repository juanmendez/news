//
//  QueryArticleEntity.swift
//  news
//
//  Created by Mendez, Juan on 11/7/25.
//

import Foundation
import GRDB

struct QueryArticleEntity: Codable, Equatable, FetchableRecord, PersistableRecord  {
    var id: Int64? // Auto-incremented primary key
    var queryName: String
    var articleId: String
    
    init(id: Int64? = nil, queryName: String, articleId: String) {
        self.id = id
        self.queryName = queryName
        self.articleId = articleId
    }
    
    enum Columns {
        static let id = Column(CodingKeys.id)
        static let queryname = Column(CodingKeys.queryName)
        static let articleId = Column(CodingKeys.articleId)
    }
}
