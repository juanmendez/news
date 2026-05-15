//
//  MainTabView.swift
//  news
//
//  Created by Mendez, Juan on 2/7/26.
//

import SwiftUI

enum Tab: Hashable {
    case articles
    case queries
}

struct ContentView: View {
    @State private var selectedTab: Tab = .articles
    @State private var selectedQuery: String = ""

    var body: some View {
        TabView(selection: $selectedTab) {
            ArticlesView(
                viewModelContract: ArticlesViewModel(),
                selectedQuery: $selectedQuery
            )
            .tabItem {
                Label("Articles", systemImage: "newspaper.fill")
            }
            .tag(Tab.articles)

            QueriesView(
                contract: QueriesViewModel(),
                onQuerySelected: { query in
                    selectedQuery = query.queryName
                    selectedTab = .articles
                }
            )
            .tabItem {
                Label("History", systemImage: "bookmark")
            }
            .tag(Tab.queries)
        }
        .tabBarMinimizeBehavior(.onScrollDown)
    }
}

#Preview {
    ContentView()
}
