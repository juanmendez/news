//
//  ArticlesResponse.swift
//  news
//
//  Created by Mendez, Juan on 10/13/25.
//

import Foundation

struct ArticlesResponse: Codable, Equatable {
    let status: String
    let totalResults: Int
    let articles: [Article]
}
