//
//  ArticleEntity+DateCreated.swift
//  news
//
//  Created by Mendez, Juan on 2/10/26.
//

import Foundation

extension ArticleEntity {
    /// Converts the Int64 timestamp (milliseconds since Unix epoch) to a Date object
    nonisolated var publishedDate: Date {
        Date(timeIntervalSince1970: TimeInterval(publishedAt) / 1000.0)
    }

    /// Formats the published date as a human-readable string
    /// - Parameter style: The date formatter style (default: .medium)
    /// - Returns: Formatted date string
    nonisolated func formattedPublishedDate(style: DateFormatter.Style = .medium) -> String {
        let formatter = DateFormatter()
        formatter.dateStyle = style
        formatter.timeStyle = .none
        return formatter.string(from: publishedDate)
    }

    /// Returns a relative time string like "2 hours ago" or "3 days ago"
    nonisolated var relativePublishedTime: String {
        let formatter = RelativeDateTimeFormatter()
        formatter.unitsStyle = .full
        return formatter.localizedString(for: publishedDate, relativeTo: Date())
    }

    nonisolated var imageAsUrl: URL? {
        URL(string: imageUrl)
    }
}
