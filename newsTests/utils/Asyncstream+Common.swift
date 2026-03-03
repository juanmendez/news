//
//  Asyncstream+Common.swift
//  newsTests
//
//  Created by Mendez, Juan on 10/24/25.
//

import Foundation

extension AsyncStream {
    func collect() async -> [Element] {
        var result: [Element] = []

        for await value in self {
            result.append(value)
        }

        return result
    }
}
