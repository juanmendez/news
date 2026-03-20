//
//  MockInternetService.swift
//  news
//
//  Created by Mendez, Juan on 3/2/26.
//

import Foundation
@testable import news

// MARK: - MockInternetService

/// A hand-written mock for the InternetService protocol.
/// Set `access` to control what `hasAccess()` returns.
@MainActor
final class MockInternetService: InternetService {

    // MARK: - Stub

    /// Set this before calling `hasAccess()` to control what the mock returns.
    var access: Bool = true

    // MARK: - hasAccess()

    func hasAccess() async -> Bool {
        access
    }
}
