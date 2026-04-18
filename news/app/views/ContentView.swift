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
    @State private var selectedQuery: String = ""
    @State private var selectedTab: Tab = .articles

    var body: some View {
        TabView(selection: $selectedTab) {
            ArticlesView(
                contract: ArticlesViewModel(),
                selectedQuery: selectedQuery
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
