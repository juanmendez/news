//
//  ArticlesViewModelPreview.swift
//  news
//
//  Created by Mendez, Juan on 1/27/26.
//

import Foundation

struct ArticlesViewModelPreview: ArticlesViewModelContract {
    var articles: [ArticleEntity] = []
    var errorMessage: String?
    var isScrollingFinished: Bool = true
    var scrollToTop: Bool = false
    var query: String = ""
    var articleRead: ArticleEntity? = nil

    func fetchArticles() async { }
    func refreshArticles() async { }
    func submitArticles() async { }
}
