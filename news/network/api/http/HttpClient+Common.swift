//
//  HttpClient+Common.swift
//  news
//
//  Created by Mendez, Juan on 10/13/25.
//

import Foundation

extension HttpClient {

    @discardableResult
    func request<T: Decodable & Sendable>(
        router: HttpRouter,
        headers: [String: String]?,
        queryItems: [URLQueryItem]?,
        body: Data?
    ) async throws -> HttpClientResponse<T> {
        let httpClientResponseRaw = try await rawRequest(
            router: router,
            headers: headers,
            queryItems: queryItems,
            body: body
        )

        let model = try DecoderFactory.iso8601Decoder.decode(T.self, from: httpClientResponseRaw.data)
        return HttpClientResponse(model: model, response: httpClientResponseRaw.response)
    }
}
