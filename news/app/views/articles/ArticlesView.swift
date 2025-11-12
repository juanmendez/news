//
//  ArticlesView.swift
//  news
//
//  Created by Mendez, Juan on 11/11/25.
//

import SwiftUI

struct ArticlesView: View {
    @StateObject private var viewModel = ArticlesViewModel()

    var body: some View {
        List {
            ForEach(viewModel.articles, id: \.id) { article in
                Text(article.title)
            }
        }
        .padding()
        .onAppear {
            Task {
                await viewModel.getArticles(query: "Top Headlines")
            }
        }
    }
}

#Preview {
    ArticlesView()
}
