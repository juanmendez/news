//
//  ArticleEntityMapper.swift
//  news
//
//  Created by Mendez, Juan on 10/14/25.
//

import Foundation

struct ArticleEntityMapper: EntityMapper {
    typealias EntityModel = ArticleEntity
    typealias DomainModel = Article

    func toDomain(_ entityModel: ArticleEntity) -> Article {
        return Article(
            id: entityModel.id,
            source: Source(id: entityModel.sourceId, name: entityModel.sourceName),
            author: entityModel.author,
            title: entityModel.title,
            description: entityModel.description,
            url: entityModel.url,
            urlToImage: entityModel.imageUrl,
            publishedAt: Date(timeIntervalSince1970: TimeInterval(entityModel.publishedAt) / 1000),
            content: entityModel.content
        )
    }

    nonisolated func toEntity(_ domainModel: Article) -> ArticleEntity {
        return ArticleEntity(
            id: domainModel.id,
            sourceId: domainModel.source.id,
            sourceName: domainModel.source.name,
            author: domainModel.author,
            title: domainModel.title,
            description: domainModel.description,
            url: domainModel.url,
            imageUrl: domainModel.urlToImage,
            publishedAt: Int64(domainModel.publishedAt.timeIntervalSince1970 * 1000),
            content: domainModel.content
        )
    }
}
