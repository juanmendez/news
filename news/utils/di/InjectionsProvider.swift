//
//  InjectionsProvider.swift
//  news
//
//  Created by Mendez, Juan on 10/18/25.
//

struct InjectionsProvider {
    static private var injections: Injections = EmptyInjections()

    static func register(_ injections: any Injections) {
        InjectionsProvider.self.injections = injections
    }

    static func byType<T>(_ type: T.Type) -> T {
        injections.byType(type)
    }
}

struct EmptyInjections: Injections {
    var dependencies: [InjectionType: Any] = [:]
}
