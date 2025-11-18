//
//  HttpClient.swift
//  news
//
//  Created by Mendez, Juan on 10/7/25.
//

import Foundation

protocol HttpClient {
    @discardableResult
    func rawRequest(
        router: HttpRouter,
        headers: [String: String]?,
        queryItems: [URLQueryItem]?,
        body: Data?
    ) async throws -> HttpClientResponseRaw
}
