//
//  QueriesViewModel.swift
//  news
//
//  Created by Mendez, Juan on 4/13/26.
//

import Foundation

@Observable
class QueriesViewModel: QueriesViewModelContract {
    private(set) var queries: [QueryEntity] = []
    private let repository: Repository

    init(repository: Repository = InjectionProvider.byType(Repository.self)) {
        self.repository = repository
    }

    func refreshQueries() async {
        // Fetch queries from the repository and assign to self.queries
        var loadedQueries: [QueryEntity] = []
        for await resource in repository.getQueries() {
            if case let .success(item) = resource {
                loadedQueries = item
            }
        }
        self.queries = loadedQueries
    }
}
