//
//  Stub.swift
//  news
//
//  Created by Mendez, Juan on 1/27/26.
//

import Foundation

protocol Stub {
    nonisolated(unsafe) static var stub: Self { get }
}

extension Bool: Stub {
    nonisolated(unsafe) static var stub: Bool {
        false
    }
}

extension String: Stub {
    nonisolated(unsafe) static var stub: String {
        ""
    }
}

extension Date: Stub {
    nonisolated(unsafe) static var stub: Date {
        Date()
    }
}

extension UUID: Stub {
    nonisolated(unsafe) static var stub: UUID {
        UUID()
    }
}

extension Int: Stub {
    nonisolated(unsafe) static var stub: Int {
        .zero
    }
}

extension Int64: Stub {
    nonisolated(unsafe) static var stub: Int64 {
        .zero
    }
}

extension Float: Stub {
    nonisolated(unsafe) static var stub: Float {
        .zero
    }
}

extension Double: Stub {
    nonisolated(unsafe) static var stub: Double {
        .zero
    }
}

extension Array: Stub {
    nonisolated(unsafe) static var stub: [Element] {
        []
    }
}

extension Dictionary: Stub {
    nonisolated(unsafe) static var stub: [Key: Value] {
        [:]
    }
}

extension Set: Stub {
    nonisolated(unsafe) static var stub: Set<Element> {
        []
    }
}
