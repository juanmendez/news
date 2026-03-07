//
//  ArticlesView.swift
//  news
//
//  Created by Mendez, Juan on 11/11/25.
//

import SwiftUI

struct ArticlesView: View {
    @State private var viewModelContract: ArticlesViewModelContract

    init(contract: ArticlesViewModelContract) {
        self.viewModelContract = contract
    }

    var body: some View {
        NavigationStack {
            ScrollViewReader { proxy in
                content
                    .navigationTitle("app_name")
                    .navigationBarTitleDisplayMode(.inline)
                    .articlesToolbar(
                        query: $viewModelContract.query,
                        onRefresh: viewModelContract.refreshArticles,
                        onSubmit: viewModelContract.submitArticles
                    )
                    .refreshable {
                        await viewModelContract.refreshArticles()
                    }
                    .alert(
                        viewModelContract.errorMessage ?? "",
                        isPresented: Binding(
                            get: { viewModelContract.errorMessage != nil },
                            set: { if !$0 { viewModelContract.errorMessage = nil } }
                        )
                    ) { }
                    .onChange(of: viewModelContract.scrollToTop) { _, newValue in
                        if newValue {
                            withAnimation(.default) {
                                proxy.scrollTo("top", anchor: .top)
                            } completion: {
                                viewModelContract.scrollToTop = false
                            }
                        }
                    }
            }
        }
    }

    @ViewBuilder
    private var content: some View {
        List {
            ForEach(viewModelContract.articles, id: \.id) { article in
                ArticleCardView(article)
            }

            // Create an Infinitely Scrolling List in SwiftUI
            // https://tinyurl.com/2bzznj8s
            if !viewModelContract.isScrollingFinished {
                ProgressBar()
                    .onAppear {
                        Task {
                            await viewModelContract.fetchArticles()
                        }
                    }
            }
        }
        .listStyle(.plain)
    }
}


#Preview("loading") {
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
            isScrollingFinished: false,
        )
    )
}

#Preview("with articles") {
    ArticlesView(
        contract: ArticlesViewModelPreview(
            articles: Array(PreviewConstants.articleEntities.prefix(8)),
            isScrollingFinished: false,
        )
    )
}

#Preview("with articles fully loaded") {
    ArticlesView(
        contract: ArticlesViewModelPreview(
            articles: Array(PreviewConstants.articleEntities.prefix(8)),
            isScrollingFinished: true,
        )
    )
}
