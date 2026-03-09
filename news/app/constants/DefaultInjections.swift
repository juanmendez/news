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

        do {
            let database = try DefaultNewsDatabase.create()
            dependencies[.database] = database
            
            let repository = DefaultRepository(httpClient: httpClient, apiKey: NewsApi.key, database: database)
            dependencies[.repository] = repository
        } catch {
            Log.e("error \(error)")
        }


        dependencies[.internetService] = DefaultInternetService()
    }
}
