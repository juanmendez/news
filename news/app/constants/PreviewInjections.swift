//
//  PreviewInjections.swift
//  news
//
//  Created by Mendez, Juan on 4/1/26.
//

import Foundation
import GRDB

struct PreviewInjections: Injections {
    var dependencies: [InjectionType: Any] = [:]

    init() {
        dependencies[.internetService] = PreviewInternetService()
    }
}

struct PreviewInternetService: InternetService {
    func hasAccess() async -> Bool {
        false
    }
}
