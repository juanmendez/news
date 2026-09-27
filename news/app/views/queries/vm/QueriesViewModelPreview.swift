//
//  PreviewQueriesViewModel.swift
//  news
//
//  Created by Mendez, Juan on 4/17/26.
//

import Foundation

class QueriesViewModelPreview: QueriesViewModelContract {
    var queries: [QueryEntity]

    init(queries: [QueryEntity] = PreviewConstants.queries) {
        self.queries = queries
    }

    func refreshQueries() async { }

    func deleteQueries(_ deletedQueries: [QueryEntity]) async {
        for query in deletedQueries {
            if let index = queries.firstIndex(of: query) {
                queries.remove(at: index)
            }
        }
    }
}
