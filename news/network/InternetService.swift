//
//  InternetService.swift
//  news
//
//  Created by Mendez, Juan on 3/2/26.
//

import Foundation

protocol InternetService {
    /// Returns `true` if the device has an active internet connection.
    func hasAccess() async -> Bool
}
