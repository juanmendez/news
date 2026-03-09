//
//  InjectionsProvider.swift
//  news
//
//  Created by Mendez, Juan on 10/18/25.
//

enum InjectionProvider {
    @MainActor static private var injections: Injections = EmptyInjections()

    @MainActor static func register(_ injections: any Injections) {
        Self.injections = injections
    }

    @MainActor static func byType<T>(_ type: T.Type) -> T {
        injections.byType(type)
    }
}

struct EmptyInjections: Injections {
    var dependencies: [InjectionType: Any] = [:]
}
