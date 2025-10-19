//
//  InjectionsProvider.swift
//  news
//
//  Created by Mendez, Juan on 10/18/25.
//

struct InjectionsProvider {
    static private(set) var injections: Injections = EmptyInjections()

    static func register(_ injections: any Injections) {
        InjectionsProvider.self.injections = injections
    }
}

struct EmptyInjections: Injections {
    var dependencies: [InjectionType: Any] = [:]
}
