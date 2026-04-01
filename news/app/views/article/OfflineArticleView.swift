//
//  OfflineArticleView.swift
//  news
//
//  Created by Mendez, Juan on 3/31/26.
//

import Kingfisher
import SwiftUI

struct OfflineArticleView: View {
    // TODO: add VM, in order to format data and avoid logic in View.
    let articleEntity: ArticleEntity
    var checkIfOnline: () async -> Void = {}

    var body: some View {
        ScrollView {
            KFImage(articleEntity.imageAsUrl)
                .placeholder {
                    HStack {
                        Spacer()
                        ProgressView()
                        Spacer()
                    }
                }
                .onFailureView {
                    Image(systemName: "photo")
                        .resizable()
                        .scaledToFit()
                        .foregroundColor(.gray)
                        .padding(.horizontal, 8)
                }
                .resizable()
                .scaledToFit()
                .padding(.bottom, 16)

            VStack(alignment: .leading, spacing: 8) {
                Text(articleEntity.title)
                    .font(.headline)
                    .fontWeight(.heavy)

                Text("by \(articleEntity.author), \(articleEntity.sourceName)")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                Text(articleEntity.publishedDate.formatted())
                    .font(.caption)
                    .foregroundColor(.secondary)
                
                Text(articleEntity.content)
                    .font(.callout)

                if let url = articleEntity.asUrl {
                    Button("open in browser") {
                        UIApplication.shared.open(url)
                    }
                }
            }
            .padding(.horizontal, 8)
        }
        .refreshable {
            await checkIfOnline()
        }
    }
}

#Preview {
    let article = PreviewConstants.articleEntities.first!
    OfflineArticleView(
        articleEntity: article
    )
}

#Preview {
    let article = PreviewConstants.articleEntities.first!
    NavigationStack {
        OfflineArticleView(articleEntity: article)
            .navigationTitle(article.title)
            .navigationBarTitleDisplayMode(.inline)
    }
}
