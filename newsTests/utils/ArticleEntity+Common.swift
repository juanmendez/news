//
//  ArticleEntity+Common.swift
//  newsTests
//
//  Created by Mendez, Juan on 3/5/26.
//

import Foundation

@testable import news

extension Array where Element == ArticleEntity {

    /// Returns the array sorted alphabetically by article `id`.
    func sortedById() -> [ArticleEntity] {
        sorted(by: { $0.id < $1.id })
    }
}
