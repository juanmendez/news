//
//  ArticlesViewModel.swift
//  news
//
//  Created by Mendez, Juan on 11/11/25.
//

import Foundation

class ArticlesViewModel: ObservableObject {

    @Published private(set) var isLoading: Bool = false
    @Published private(set) var articles: [ArticleEntity] = []
    @Published private(set) var error: Error?
    private var repository: Repository

    init(repository: Repository = InjectionsProvider.byType(Repository.self)) {
        self.repository = repository
    }

    @MainActor func getArticles(query: String) async {
        for await value in repository.getArticles(query: query, page: 1) {
            switch value {
                case .loading(item: let item):
                    isLoading = true
                    articles = item ?? []
                case .error(error: let error):
                    isLoading = false
                    self.error = error
                case .success(item: let item):
                    isLoading = false
                    articles = item
            }
        }
    }
}
