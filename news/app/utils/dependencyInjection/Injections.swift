//
//  Injections.swift
//  news
//
//  Created by Mendez, Juan on 10/18/25.
//

import Foundation

protocol Injections {
    var dependencies: [InjectionType: Any] { get }
}

extension Injections {
    func byType<T>(_ type: T.Type) -> T {

        let injectionType: InjectionType =
            if type.self == HttpClient.self {
                .httpClient
            } else if type.self == Repository.self {
                .repository
            } else if type.self == NewsDatabase.self {
                .database
            } else if type.self == InternetService.self {
                .internetService
            } else {
                fatalError("No injection type defined for expected type \(type)")
            }

        guard let value = dependencies[injectionType] as? T else {
            fatalError("No dependency available for expected type \(type)")
        }

        return value
    }
}
