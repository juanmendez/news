//
//  Stub.swift
//  news
//
//  Created by Mendez, Juan on 1/27/26.
//

import Foundation

protocol Stub {
    static var stub: Self { get }
}

extension Bool: Stub {
    static var stub: Bool {
        false
    }
}

extension String: Stub {
    static var stub: String {
        ""
    }
}

extension Date: Stub {
    static var stub: Date {
        Date()
    }
}

extension UUID: Stub {
    static var stub: UUID {
        UUID()
    }
}

extension Int: Stub {
    static var stub: Int {
        .zero
    }
}

extension Int64: Stub {
    static var stub: Int64 {
        .zero
    }
}

extension Float: Stub {
    static var stub: Float {
        .zero
    }
}

extension Double: Stub {
    static var stub: Double {
        .zero
    }
}

extension Array: Stub {
    static var stub: [Element] {
        []
    }
}

extension Dictionary: Stub {
    static var stub: [Key: Value] {
        [:]
    }
}

extension Set: Stub {
    static var stub: Set<Element> {
        []
    }
}
