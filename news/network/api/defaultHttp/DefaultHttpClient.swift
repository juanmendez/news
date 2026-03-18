//
//  DefaultHttpClient.swift
//  news
//
//  Created by Mendez, Juan on 10/13/25.
//

import Foundation

struct DefaultHttpClient: Sendable, HttpClient {
    // MARK: - Public Properties

    var urlBase: String = ""

    // MARK: - Public Functions

    func rawRequest(
        router: any HttpRouter,
        headers: [String: String]?,
        queryItems: [URLQueryItem]?,
        body: Data?
    ) async throws -> HttpClientResponseRaw {
        let urlRequest = try await URLRequest.make(
            urlBase: urlBase,
            router: router,
            headers: headers,
            queryItems: queryItems,
            body: body
        )

        let (data, urlResponse) = try await URLSession.shared.data(for: urlRequest)

        guard let urlResponse = urlResponse as? HTTPURLResponse else {
            throw HttpError.noHttpResponse
        }

        switch urlResponse.statusCode {
        case 200...299:
            return HttpClientResponseRaw((data, urlResponse))
        case 401...404:
            throw HttpError.notFound
        case 409:
            let errorModels = try DecoderFactory.iso8601Decoder.decode(
                [ErrorModel].self,
                from: data
            )
            throw HttpError.conflict(result: errorModels)
        case 429:
            throw HttpError.tooManyRequests
        default:
            throw HttpError.badResponse(
                status: urlResponse.statusCode,
                error: String(data: data, encoding: .utf8),
                result: try DecoderFactory.iso8601Decoder.decode([ErrorModel].self, from: data)
            )
        }
    }
}
