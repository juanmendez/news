//
//  HttpClientResponseRaw+Common.swift
//  newsTests
//
//  Created by Mendez, Juan on 2/26/26.
//

import Foundation
@testable import news

extension HttpClientResponseRaw {
    /// Creates an `HttpClientResponseRaw` from any `Encodable` Swift type.
    ///
    /// Encodes `item` to JSON `Data` using `JSONEncoder` and builds a real
    /// `HTTPURLResponse` with the provided parameters, then delegates to
    /// the existing tuple initializer. Useful in tests to avoid hand-writing
    /// raw JSON strings.
    ///
    /// - Parameters:
    ///   - item: Any `Encodable` Swift value to be encoded as the response body.
    ///   - statusCode: The HTTP status code for the response. Defaults to `200`.
    ///   - url: The URL string for the response. Defaults to `"https://newsapi.org"`.
    ///   - headerFields: HTTP header fields for the response. Defaults to an empty dictionary.
    /// - Throws: `EncodingError` if `item` cannot be encoded to JSON.
    ///
    /// ## Example
    /// ```swift
    /// let article = Article(source: ..., author: "John", ...)
    /// sut.rawResponse = try HttpClientResponseRaw(item: article)
    ///
    /// // With custom parameters:
    /// sut.rawResponse = try HttpClientResponseRaw(
    ///     item: article,
    ///     statusCode: 404,
    ///     url: "https://techcrunch.com",
    ///     headerFields: ["Authorization": "Bearer token"]
    /// )
    /// ```

    @MainActor
    init<T: Encodable>(
        item: T,
        statusCode: Int = 200,
        url: String = "https://newsapi.org",
        headerFields: [String: String] = [:]
    ) throws {
        let data = try DecoderFactory.iso8601Encoder.encode(item)
        
        guard let url = URL(string: url) else {
            throw HttpError.invalidUrl
        }
        
        guard let response = HTTPURLResponse(
            url: url,
            statusCode: statusCode,
            httpVersion: nil,
            headerFields: headerFields
        ) else {
            throw HttpError.noHttpResponse
        }
        
        self.init((data: data, response: response))
    }
}
