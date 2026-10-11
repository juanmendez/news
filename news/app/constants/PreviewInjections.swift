//
//  PreviewInjections.swift
//  news
//
//  Created by Mendez, Juan on 4/1/26.
//

import Foundation
import SwiftData

struct PreviewInjections: Injections {
    var dependencies: [InjectionType: Any] = [:]

    init(modelContext: ModelContext) {
        dependencies[.database] = DefaultNewsDatabase(modelContext: modelContext)
        dependencies[.internetService] = PreviewInternetService()
    }
}

struct PreviewInternetService: InternetService {
    func hasAccess() async -> Bool {
        false
    }
}
