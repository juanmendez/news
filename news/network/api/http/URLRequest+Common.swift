//
//  UrlRequest+Common.swift
//  news
//
//  Created by Mendez, Juan on 10/13/25.
//

import Foundation

extension URLRequest {
    static func make(
        urlBase: String,
        router: HttpRouter,
        headers: [String: String]?,
        queryItems: [URLQueryItem]?,
        body: Data?
    ) async throws -> URLRequest {

        var component = URLComponents(string: urlBase)
        component?.path += router.path
        component?.queryItems = queryItems

        guard let component = component,
              let url = component.url else {
            throw HttpError.invalidUrl
        }

        var request = URLRequest(url: url)
        request.httpMethod = router.httpMethod.rawValue

        if let headers = headers {
            for (key, value) in headers {
                request.setValue(value, forHTTPHeaderField: key)
            }
        }

        if let body = body {
            request.httpBody = body
            request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        }

        return request
    }
}
