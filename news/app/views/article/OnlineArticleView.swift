//
//  OnlineArticleView.swift
//  news
//
//  Created by Mendez, Juan on 3/31/26.
//

import SwiftUI
import WebKit


struct OnlineArticleView: View {
    let articleEntity: ArticleEntity
    @State private var isLoading = true

    var body: some View {
        ZStack {
            if let url = articleEntity.asUrl {
                WebView(url: url, isLoading: $isLoading)
            }

            if isLoading {
                HStack {
                    Spacer()
                    ProgressView()
                    Spacer()
                }
                .frame(maxWidth: .infinity)
            }
        }
    }
}

struct WebView: UIViewRepresentable {
    let url: URL
    @Binding var isLoading: Bool

    func makeCoordinator() -> Coordinator {
        Coordinator(isLoading: $isLoading)
    }

    func makeUIView(context: Context) -> WKWebView {
        let webView = WKWebView()
        webView.navigationDelegate = context.coordinator
        return webView
    }

    func updateUIView(_ uiView: WKWebView, context: Context) {
        uiView.load(URLRequest(url: url))
    }

    class Coordinator: NSObject, WKNavigationDelegate {
        @Binding var isLoading: Bool

        init(isLoading: Binding<Bool>) {
            _isLoading = isLoading
        }

        func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
            isLoading = false
        }
    }
}

#Preview {
    let article = PreviewConstants.articleEntities.first!
    NavigationStack {
        OnlineArticleView(articleEntity: article)
            .navigationTitle(article.title)
            .navigationBarTitleDisplayMode(.inline)
    }
}
