//
//  QueriesView.swift
//  news
//
//  Created by Mendez, Juan on 4/17/26.
//

import SwiftUI

struct QueriesView: View {
    @State private var viewModelContract: QueriesViewModelContract

    init(contract: QueriesViewModelContract) {
        self._viewModelContract = State(initialValue: contract)
    }

    var body: some View {
        List(viewModelContract.queries, id: \.queryName) { query in
            Text(query.queryName)
        }
        .refreshable {
            await viewModelContract.refreshQueries()
        }
        .task {
            await viewModelContract.refreshQueries()
        }
    }
}

#Preview {
    QueriesView(contract: QueriesViewModelPreview())
}
