//
//  PreviewQueriesViewModel.swift
//  news
//
//  Created by Mendez, Juan on 4/17/26.
//

import Foundation

struct QueriesViewModelPreview: QueriesViewModelContract {
    var queries: [QueryEntity] {
        PreviewConstants.queries
    }

    func refreshQueries() async { }
}
