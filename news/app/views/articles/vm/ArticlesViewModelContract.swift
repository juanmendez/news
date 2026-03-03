//
//  ArticlesViewModelContract.swift
//  news
//
//  Created by Mendez, Juan on 1/27/26.
//

import Foundation

@MainActor 
protocol ArticlesViewModelContract {
    var showProgress: Bool { get }
    var articles: [ArticleEntity] { get }
    var errorMessage: String? { get set }
    var isScrollingFinished: Bool { get }
    var query: String { get set }

    func fetchArticles(refresh: Bool) async
    func submitArticles() async
}
