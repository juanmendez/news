//
//  DefaultInternetService.swift
//  news
//
//  Created by Mendez, Juan on 3/2/26.
//

import Foundation

final class DefaultInternetService: InternetService {

    /// Probes Apple's success URL to determine if the device has internet access.
    /// - Returns: `true` if the probe returns HTTP 200, `false` otherwise.
    func hasAccess() async -> Bool {
        guard let url = URL(string: "https://www.apple.com/library/test/success.html") else {
            return false
        }

        var request = URLRequest(url: url)
        request.timeoutInterval = 5.0

        do {
            let (_, response) = try await URLSession.shared.data(for: request)
            if let httpResponse = response as? HTTPURLResponse {
                return httpResponse.statusCode == 200
            }
            return false
        } catch {
            // Covers timeout (URLError.timedOut), no connection, and other errors
            return false
        }
    }
}
