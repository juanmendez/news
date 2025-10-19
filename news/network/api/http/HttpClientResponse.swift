//
//  HttpClientResponse.swift
//  news
//
//  Created by Mendez, Juan on 10/7/25.
//

import Foundation

struct HttpClientResponse<T: Decodable & Sendable> {
    var model: T
    var response: HTTPURLResponse
}
