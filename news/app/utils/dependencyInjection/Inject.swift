//
//  Injections.swift
//  news
//
//  Created by Mendez, Juan on 10/18/25.
//

import Foundation

@propertyWrapper
struct Inject<T> {
    @MainActor
    var wrappedValue: T {
        InjectionProvider.byType(T.self)
    }
}
