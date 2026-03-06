//
//  ArticlesViewModelPreview.swift
//  news
//
//  Created by Mendez, Juan on 1/27/26.
//

import Foundation

struct ArticlesViewModelPreview: ArticlesViewModelContract {
    var showProgress: Bool = false
    var articles: [ArticleEntity] = []
    var errorMessage: String?
    var isScrollingFinished: Bool = true
    var query: String = ""

    func fetchArticles() async { }
    func refreshArticles() async { }

    func submitArticles() async { }
}
