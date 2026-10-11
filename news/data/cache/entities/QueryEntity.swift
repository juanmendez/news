//
//  QueryEntity.swift
//  news
//
//  Created by Mendez, Juan on 11/7/25.
//

import Foundation

nonisolated
struct QueryEntity: Codable, Equatable, Hashable, Sendable  {
    let queryName: String
}
