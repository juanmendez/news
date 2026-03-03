//
//  DefaultInjections.swift
//  news
//
//  Created by Mendez, Juan on 10/18/25.
//

import Foundation
import GRDB

struct DefaultInjections: Injections {
    var dependencies: [InjectionType: Any] = [:]
    
    init() {
        let httpClient = DefaultHttpClient(urlBase: "https://newsapi.org")
        dependencies[.httpClient] = httpClient
        
        let database: NewsDatabase
        do {
            database = try DefaultNewsDatabase.create()
        } catch {
            Log.e("error \(error)")
            database = SessionNewsDatabase()
        }

        dependencies[.database] = database
        let repository = DefaultRepository(httpClient: httpClient, apiKey: NewsApi.key, database: database)
        dependencies[.repository] = repository
    }
}
