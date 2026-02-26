//
//  ArticlesViewModel.swift
//  news
//
//  Created by Mendez, Juan on 11/11/25.
//

import Foundation

@Observable
class ArticlesViewModel: ArticlesViewModelContract {

    private(set) var showProgress: Bool = false
    private(set) var articles: [ArticleEntity] = []
    var errorMessage: String?
    private(set) var isScrollingFinished: Bool = false

    // TODO: start with this initial query, but allow user to search by query as well.
    var query: String = "Top Headlines"
    private var page: Int = 0
    private let pageSize: Int
    private var repository: Repository

    init(
        repository: Repository = InjectionsProvider.byType(Repository.self),
        pageSize: Int = 10
    ) {
        self.repository = repository
        self.pageSize = pageSize
    }

    @MainActor private func getArticles(refresh: Bool = false) async {
        for await value in repository.getArticles(query: query, page: page, pageSize: pageSize, refresh: refresh) {
            showProgress = value.isLoading

            switch value {
                case .loading(let item):
                    articles = item ?? []
                case .error(_):
                    isScrollingFinished = true
                    self.errorMessage = String(localized: "Something went wrong")
                case .success(let item):
                    isScrollingFinished = articles == item
                    articles = item
            }
        }
    }

    @MainActor func fetchArticles(refresh: Bool = false) async {
        if !showProgress {
            if refresh {
                page = 0
            }

            page += 1

            await self.getArticles(refresh: refresh)
        }
    }

    @MainActor func submitArticles() async {
        page = 1
        isScrollingFinished = false
        showProgress = false
        await getArticles(refresh: false)
    }
}
