//
//  Articles+Log.swift
//  news
//
//  Created by Mendez, Juan on 9/16/25.
//

import Foundation

extension Array where Element == Article {
    /**
     * Logs all articles
     * @param message message logged
     * @param articles list of [Article] logged
     */
    func logArticles(_ message: String) {
        Log.i("\(message) \(self.count) articles:")

        self.enumerated().forEach { index, article in
            Log.i("\(index + 1). \(article.title) | \(article.publishedDate.formatted())")
        }
    }

    /**
     * Logs first N articles
     * @param message message logged
     * @param articles list of [Article] logged
     */
    func logLimitedArticles(message: String, maxArticles: Int = 3) {
        let limit = Swift.max(Swift.min(maxArticles, self.count), 0)
        Array(self.prefix(limit)).logArticles(message)
    }
}
