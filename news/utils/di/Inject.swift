//
//  Injections.swift
//  news
//
//  Created by Mendez, Juan on 10/18/25.
//

import Foundation

@propertyWrapper
struct Inject<T> {
    var wrappedValue: T {
        InjectionsProvider.injections.getInjectionByType(type: T.self)
    }
}
