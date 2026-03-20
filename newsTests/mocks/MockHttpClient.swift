//
//  MockHttpClient.swift
//  news
//
//  Created by Mendez, Juan on 2/24/26.
//

import Foundation

@testable import news

// MARK: - MockHttpClient

/// A hand-written mock for the HttpClient protocol.
/// Set `rawResponse` to control what rawRequest returns,
/// or leave it nil to simulate an invalidUrl error.
@MainActor
final class MockHttpClient: HttpClient {

    /// Set this before calling rawRequest to control the mock response.
    var rawResponse: HttpClientResponseRaw?

    @discardableResult
    func rawRequest(
        router: HttpRouter,
        headers: [String: String]?,
        queryItems: [URLQueryItem]?,
        body: Data?
    ) async throws -> HttpClientResponseRaw {
        guard let rawResponse else {
            throw HttpError.invalidUrl
        }
        return rawResponse
    }
}
