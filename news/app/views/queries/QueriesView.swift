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
        NavigationStack {
            List {
                if viewModelContract.queries.isEmpty {
                    Section {
                        Text("No Queries Found")
                            .font(.body)
                            .foregroundStyle(.secondary)
                            .multilineTextAlignment(.center)
                            .frame(maxWidth: .infinity, alignment: .center)
                    }
                } else {
                    ForEach(viewModelContract.queries, id: \.queryName) { query in
                        Text(query.queryName)
                    }
                }
            }
            .refreshable {
                await viewModelContract.refreshQueries()
            }
            .task {
                await viewModelContract.refreshQueries()
            }
            .navigationTitle("Search History")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}

#Preview("Empty State") {
    QueriesView(contract: QueriesViewModelPreview(queries: []))
}

#Preview("Default State") {
    QueriesView(contract: QueriesViewModelPreview())
}
