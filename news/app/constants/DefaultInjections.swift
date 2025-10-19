//
//  DefaultInjections.swift
//  news
//
//  Created by Mendez, Juan on 10/18/25.
//

import Foundation

struct DefaultInjections: Injections {
    var dependencies: [InjectionType: Any] = [:]

    init() {
        let httpClient = DefaultHttpClient(urlBase: "https://newsapi.org")
        let repository = DefaultRepository(httpClient: httpClient, apiKey: NewsApi.key)

        dependencies[.httpClient] = httpClient
        dependencies[.repository] = repository
    }
}
