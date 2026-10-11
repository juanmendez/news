//
//  DefaultInjections.swift
//  news
//
//  Created by Mendez, Juan on 10/18/25.
//

import Foundation
import SwiftData

struct DefaultInjections: Injections {
    var dependencies: [InjectionType: Any] = [:]

    init(modelContext: ModelContext) {
        let httpClient = DefaultHttpClient(urlBase: "https://newsapi.org")
        dependencies[.httpClient] = httpClient

        let database = DefaultNewsDatabase(modelContext: modelContext)
        dependencies[.database] = database

        let repository = DefaultRepository(httpClient: httpClient, apiKey: NewsApi.key, database: database)
        dependencies[.repository] = repository

        dependencies[.internetService] = DefaultInternetService()
    }
}
