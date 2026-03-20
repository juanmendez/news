//
//  ArticlesViewModel.swift
//  news
//
//  Created by Mendez, Juan on 11/11/25.
//

import Foundation

@Observable
class ArticlesViewModel: ArticlesViewModelContract {

    private var processing: Bool = false
    private(set) var articles: [ArticleEntity] = []
    var errorMessage: String?
    private(set) var isScrollingFinished: Bool = false
    var scrollToTop: Bool = false

    var query: String = TOP_HEADLINES
    private var page: Int = 0
    private let pageSize: Int
    private var repository: Repository
    private var internetService: InternetService

    init(
        repository: Repository = InjectionProvider.byType(Repository.self),
        internetService: InternetService = InjectionProvider.byType(InternetService.self),
        pageSize: Int = 10
    ) {
        self.repository = repository
        self.internetService = internetService
        self.pageSize = pageSize
    }

    private func getArticles(refresh: Bool = false) async {
        Log.i("getArticles query: \(query), page: \(page), refresh: \(refresh)")

        for await value in repository.getArticles(
            query: query,
            page: page,
            pageSize: pageSize,
            refresh: refresh
        ) {
            processing = value.isLoading

            switch value {
                case .loading(let item):
                    articles = item ?? []
                    Log.i("articles", attributes: articles.map{ $0.id })
                    scrollToTop = false
                case .error(let error):
                    isScrollingFinished = true
                    scrollToTop = false

                    if NewsApi.key.isEmpty {
                        self.errorMessage = String(localized: "no_api_key")
                    } else if (error as? HttpError) == .tooManyRequests {
                        self.errorMessage = String(localized: "too_many_requests")
                    } else {
                        self.errorMessage = String(localized: "something_went_wrong")
                    }
                case .success(let item):
                    isScrollingFinished = articles == item
                    articles = item
                    Log.p("articles", attributes: articles.map{ $0.id })
                    scrollToTop = page == 1
            }
        }
    }

    func fetchArticles() async {
        guard !processing else { return }
        page += 1
        Log.i("fetchArticles processing")
        await self.getArticles(refresh: false)
    }

    func refreshArticles() async {
        guard query.isNotBlank else { return }
        guard !processing else { return }
        guard await internetService.hasAccess() else { return }
        try? await repository.deleteArticles(query: query)
        processing = true
        articles = []

        try? await Task.sleep(for: .seconds(1))

        page = 1
        isScrollingFinished = false

        Log.i("refreshArticles processing")
        await self.getArticles(refresh: true)
    }

    func submitArticles() async {
        guard query.isNotBlank else { return }
        guard !processing else { return }
        page = 1
        isScrollingFinished = false
        articles = []
        Log.i("submitArticles")
        await getArticles(refresh: false)
    }
}
