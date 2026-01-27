//
//  ArticleEntity+Stub.swift
//  newsShared
//
//  Created by Mendez, Juan on 1/27/26.
//

import Foundation

extension ArticleEntity: Stub {
    static var stub: ArticleEntity {
        .stub()
    }

    static func stub(
        id: String = .stub,
        sourceId: String? = nil,
        sourceName: String = .stub,
        author: String = .stub,
        title: String = .stub,
        description: String = .stub,
        url: String = .stub,
        imageUrl: String = .stub,
        publishedAt: Int64 = .stub,
        content: String = .stub,
    ) -> ArticleEntity {
        ArticleEntity(
            id: id,
            sourceId: sourceId,
            sourceName: sourceName,
            author: author,
            title: title,
            description: description,
            url: url,
            imageUrl: imageUrl,
            publishedAt: publishedAt,
            content: content
        )
    }
}
