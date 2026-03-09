//
//  InternetService.swift
//  news
//
//  Created by Mendez, Juan on 3/2/26.
//

import Foundation

protocol InternetService: Sendable {
    /// Returns `true` if the device has an active internet connection.
    func hasAccess() async -> Bool
}
