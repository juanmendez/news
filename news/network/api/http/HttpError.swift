//
//  HttpError.swift
//  news
//
//  Created by Mendez, Juan on 10/13/25.
//

import Foundation

enum HttpError: Error, Equatable {
    case invalidUrl
    case noHttpResponse
    case notFound
    case conflict(result: [ErrorModel]?)
    case badResponse(status: Int, error: String?, result: [ErrorModel]?)
}
