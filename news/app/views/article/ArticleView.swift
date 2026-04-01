//
//  ArticleView.swift
//  news
//
//  Created by Mendez, Juan on 3/30/26.
//

import SwiftUI

struct ArticleView: View {
    let articleEntity: ArticleEntity
    @State private var isLoading = true
    @Inject private var internetService: InternetService
    @State private var isOnline = true

    var body: some View {
        content
            .navigationTitle(articleEntity.title)
            .navigationBarTitleDisplayMode(.inline)
            .onAppear {
                Task {
                    isOnline = await internetService.hasAccess()
                }
            }
    }

    @ViewBuilder
    private var content: some View {
        if isOnline {
            OnlineArticleView(articleEntity: articleEntity)
        } else {
            OfflineArticleView(articleEntity: articleEntity) { () async -> Void in
                isOnline = await internetService.hasAccess()
            }
        }
    }
}

#Preview {
    NavigationStack {
        ArticleView(articleEntity: PreviewConstants.articleEntities.first!)
    }
}
