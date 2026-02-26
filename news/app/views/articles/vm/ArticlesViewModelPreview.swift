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

    func fetchArticles(refresh: Bool) async { }

    func submitArticles() async { }
}
