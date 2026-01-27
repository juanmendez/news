//
//  ArticlesView.swift
//  news
//
//  Created by Mendez, Juan on 11/11/25.
//

import SwiftUI

struct ArticlesView: View {
    @State private var viewModelContract: ArticlesViewModelContract

    init(contract: ArticlesViewModelContract = ArticlesViewModel()) {
        self.viewModelContract = contract
    }

    var body: some View {
        List {
            ForEach(viewModelContract.articles, id: \.id) { article in
                Text(article.title)
            }

            // Create an Infinitely Scrolling List in SwiftUI
            // https://tinyurl.com/2bzznj8s
            if !viewModelContract.isScrollingFinished {
                Text(String(localized: "Loading"))
                    .onAppear {
                        Task {
                            await viewModelContract.fetchArticles(refresh: false)
                        }
                    }
            }
        }
        .padding()
        .refreshable {
            await viewModelContract.fetchArticles(refresh: true)
        }
    }
}

#Preview("first time landing") {
    ArticlesView(
        contract: ArticlesViewModelPreview(
            isScrollingFinished: false,
        )
    )
}

#Preview("with one article") {
    ArticlesView(
        contract: ArticlesViewModelPreview(
            articles: Array(PreviewConstants.articleEntities.prefix(1)),
            isScrollingFinished: true,
        )
    )
}

#Preview("with articles") {
    ArticlesView(
        contract: ArticlesViewModelPreview(
            articles: PreviewConstants.articleEntities,
            isScrollingFinished: true,
        )
    )
}
