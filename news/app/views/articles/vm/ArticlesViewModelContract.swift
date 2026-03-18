//
//  ArticlesViewModelContract.swift
//  news
//
//  Created by Mendez, Juan on 1/27/26.
//

import Foundation

@MainActor
protocol ArticlesViewModelContract {
    var articles: [ArticleEntity] { get }
    var errorMessage: String? { get set }
    var isScrollingFinished: Bool { get }
    var scrollToTop: Bool { get set }
    var query: String { get set }

    func fetchArticles() async
    func refreshArticles() async
    func submitArticles() async
}
