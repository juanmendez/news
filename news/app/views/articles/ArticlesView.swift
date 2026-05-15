//
//  ArticlesView.swift
//  news
//
//  Created by Mendez, Juan on 11/11/25.
//

import SwiftUI

struct ArticlesView: View {
    @State var viewModelContract: ArticlesViewModelContract
    @Binding var selectedQuery: String

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
                    .navigationDestination(item: $viewModelContract.articleRead) { article in
                        ArticleView(articleEntity: article)
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
                    .onChange(of: selectedQuery) { _, newValue in
                        if newValue.isNotEmpty, newValue != viewModelContract.query {
                            viewModelContract.query = newValue

                            Task {
                                Log.i("submitArticles: \(viewModelContract.query)")
                                await viewModelContract.submitArticles()
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
                Button {
                    viewModelContract.articleRead = article
                } label: {
                    ArticleCardView(article)
                }
            }

            // Create an Infinitely Scrolling List in SwiftUI
            // https://tinyurl.com/2bzznj8s
            if !viewModelContract.isScrollingFinished {
                ProgressBar()
                    .task {
                        Log.i("fetch articles -> \(viewModelContract.query)")
                        await viewModelContract.fetchArticles()
                    }
            }
        }
        .listStyle(.plain)
    }
}

#Preview("loading") {
    TabView {
        ArticlesView(
            viewModelContract: ArticlesViewModelPreview(
                isScrollingFinished: false,
            ),
            selectedQuery: .constant(""),
        )
        .tabItem {
            Label("Articles", systemImage: "newspaper.fill")
        }
    }
    .tabBarMinimizeBehavior(.onScrollDown)
}

#Preview("with one article") {
    TabView {
        ArticlesView(
            viewModelContract: ArticlesViewModelPreview(
                articles: Array(PreviewConstants.articleEntities.prefix(1)),
                isScrollingFinished: false,
            ),
            selectedQuery: .constant(""),
        )
        .tabItem {
            Label("Articles", systemImage: "newspaper.fill")
        }
    }
    .tabBarMinimizeBehavior(.onScrollDown)
}

#Preview("with articles") {
    TabView {
        ArticlesView(
            viewModelContract: ArticlesViewModelPreview(
                articles: Array(PreviewConstants.articleEntities.prefix(8)),
                isScrollingFinished: false,
            ),
            selectedQuery: .constant(""),
        )
        .tabItem {
            Label("Articles", systemImage: "newspaper.fill")
        }
    }
    .tabBarMinimizeBehavior(.onScrollDown)
}

#Preview("with articles fully loaded") {
    TabView {
        ArticlesView(
            viewModelContract: ArticlesViewModelPreview(
                articles: Array(PreviewConstants.articleEntities.prefix(8)),
                isScrollingFinished: true,
            ),
            selectedQuery: .constant(""),
        )
        .tabItem {
            Label("Articles", systemImage: "newspaper.fill")
        }
    }
    .tabBarMinimizeBehavior(.onScrollDown)
}
