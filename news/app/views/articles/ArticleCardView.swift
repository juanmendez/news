//
//  ArticleCardView.swift
//  news
//
//  Created by Mendez, Juan on 2/9/26.
//

import SwiftUI

struct ArticleCardView: View {
    private let article: ArticleEntity

    init(_ article: ArticleEntity) {
        self.article = article
    }

    var body: some View {
        VStack(alignment: .leading) {

            if let url = article.imageAsUrl {
                AsyncImage(url: url) { phase in
                    switch phase {
                        case .empty:
                            HStack {
                                Spacer()
                                ProgressView()
                                Spacer()
                            }
                        case .success(let image):
                            image
                                .resizable()
                                .scaledToFit()
                        case .failure:
                            Image(systemName: "photo")
                                .resizable()
                                .scaledToFit()
                                .foregroundColor(.gray)
                        @unknown default:
                            EmptyView()
                    }
                }
                .frame(height: Dimensions.articleImageHeight)
            }
            HStack {
                Text(article.sourceName)
                    .font(.caption)
                    .foregroundColor(.secondary)
                Spacer()

                Text(article.formattedPublishedDate())
                    .font(.caption)
                    .foregroundColor(.secondary)
            }

            Text(article.title)
                .font(.headline)

            Text(article.description)
                .font(.subheadline)
                .foregroundColor(.secondary)
                .lineLimit(3)
        }
        .padding(.vertical, 8)
        .listRowInsets(EdgeInsets(vertical: 0, horizontal: 16))
        .onTapGesture {

        }
    }
}

#Preview {
    ArticleCardView(PreviewConstants.articleEntities[0])
}
