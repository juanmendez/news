//
//  ContentView.swift
//  news
//
//  Created by Mendez, Juan on 9/16/25.
//

import SwiftUI

struct ContentView: View {
    @Inject var repository: Repository
    @State private var articles: [ArticleEntity] = []

    var body: some View {
        List {
            ForEach(articles, id: \.id) { article in
                Text(article.title)
            }
        }
        .padding()
        .onAppear {
            Task {
                for await value in repository.getArticles(query: "", page: 1) {
                    switch value {
                        case .loading(item: let item):
                            self.articles = item ?? []
                        case .error(error: let error):
                            print("Error: \(error)")
                        case .success(item: let item):
                            self.articles = item
                    }
                }
            }
        }
    }
}

#Preview {
    ContentView()
}
