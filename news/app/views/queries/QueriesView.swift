//
//  QueriesView.swift
//  news
//
//  Created by Mendez, Juan on 4/17/26.
//

import SwiftUI

struct QueriesView: View {
    @State private var viewModelContract: QueriesViewModelContract
    private var onQuerySelected: (QueryEntity) -> Void

    init(
        contract: QueriesViewModelContract,
        onQuerySelected: @escaping (QueryEntity) -> Void = { _ in }
    ) {
        self.viewModelContract = contract
        self.onQuerySelected = onQuerySelected
    }

    var body: some View {
        NavigationStack {
            List {
                if viewModelContract.queries.isEmpty {
                    Section {
                        Text("No History Found")
                            .font(.body)
                            .foregroundStyle(.secondary)
                            .multilineTextAlignment(.center)
                            .frame(maxWidth: .infinity, alignment: .center)
                    }
                } else {
                    ForEach(viewModelContract.queries, id: \.queryName) { query in
                        Button(action: { onQuerySelected(query) }) {
                            Text(query.queryName)
                        }
                    }
                    .onDelete { indexSet in
                        let queries = viewModelContract.queries
                        let removedQueries = indexSet.map { queries[$0] }

                        Task {
                            await viewModelContract.deleteQueries(removedQueries)
                        }
                    }
                }
            }
            .refreshable {
                await viewModelContract.refreshQueries()
            }
            .task {
                await viewModelContract.refreshQueries()
            }
            .navigationTitle("History")
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
