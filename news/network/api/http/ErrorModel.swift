//
//  ErrorResult.swift
//  news
//
//  Created by Mendez, Juan on 10/13/25.
//

import Foundation

struct ErrorModel: Decodable, Equatable {
    let type: String?
    let description: String?
    let details: [ErrorDetails]
}

struct ErrorDetails: Decodable, Equatable {
    let code: String
    let message: String
}
