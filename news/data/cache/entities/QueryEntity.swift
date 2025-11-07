//
//  QueryEntity.swift
//  news
//
//  Created by Mendez, Juan on 11/7/25.
//

import Foundation
import GRDB

struct QueryEntity: Codable, Equatable, FetchableRecord, PersistableRecord  {
    var queryName: String

    enum Columns {
        static let queryName = Column(CodingKeys.queryName)
    }
}
