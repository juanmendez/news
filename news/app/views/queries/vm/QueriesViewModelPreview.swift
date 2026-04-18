//
//  PreviewQueriesViewModel.swift
//  news
//
//  Created by Mendez, Juan on 4/17/26.
//

import Foundation

struct QueriesViewModelPreview: QueriesViewModelContract {
    var queries: [QueryEntity]

    init(queries: [QueryEntity] = PreviewConstants.queries) {
        self.queries = queries
    }

    func refreshQueries() async { }
}
