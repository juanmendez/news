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

            // Create an Infinitely Scrolling List in SwiftUI
            // https://tinyurl.com/2bzznj8s
            if !viewModel.isScrollingFinished {
                Text("Loading")
                    .onAppear {
                        Task {
                            await viewModel.fetchArticles()
                        }
                    }
            }
        }
        .padding()
    }
}

#Preview {
    ArticlesView()
}
