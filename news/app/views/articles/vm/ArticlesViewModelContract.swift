//
//  ArticlesViewModelContract.swift
//  news
//
//  Created by Mendez, Juan on 1/27/26.
//

import Foundation

protocol ArticlesViewModelContract {
    var showProgress: Bool { get }
    var articles: [ArticleEntity] { get }
    var errorMessage: String? { get }
    var isScrollingFinished: Bool { get }

    func fetchArticles(refresh: Bool) async
}
