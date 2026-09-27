//
//  QueryEntity.swift
//  news
//
//  Created by Mendez, Juan on 11/7/25.
//

import Foundation
import GRDB
import SwiftData

struct QueryEntity: Codable, Equatable, FetchableRecord, PersistableRecord {
    var queryName: String

    enum Columns {
        static let queryName = Column(CodingKeys.queryName)
    }
}

@Model
class QueryModel {
    @Attribute(.unique)
    var queryName: String

    @Relationship(deleteRule: .cascade)
    var queryArticle: [QueryArticleModel] = []

    init(queryName: String) {
        self.queryName = queryName
    }
}
