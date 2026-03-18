//
//  MainTabView.swift
//  news
//
//  Created by Mendez, Juan on 2/7/26.
//

import SwiftUI

struct ContentView: View {
    var body: some View {
        TabView {
            ArticlesView(contract: ArticlesViewModel())
                .tabItem {
                    Label("Articles", systemImage: "newspaper.fill")
                }
        }
        .tabBarMinimizeBehavior(.onScrollDown)
    }
}

#Preview {
    ContentView()
}
