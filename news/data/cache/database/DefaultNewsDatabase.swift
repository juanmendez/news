//
//  DefaultNewsDatabase.swift
//  news
//
//  Created by Mendez, Juan on 10/9/26.
//

import Foundation
import SwiftData

@MainActor
struct DefaultNewsDatabase: NewsDatabase {

    let modelContext: ModelContext

    func saveArticle(_ query: String, articleEntity: ArticleEntity) throws {
        let queryModel = try fetchOrCreateQuery(query)
        let articleId = articleEntity.id

        let descriptor = FetchDescriptor<ArticleModel>(
            predicate: #Predicate { $0.id == articleId }
        )

        if let existingArticle = try modelContext.fetch(descriptor).first {
            if !existingArticle.queries.contains(where: { $0.queryName == query }) {
                existingArticle.queries.append(queryModel)
            }
        } else {
            let articleModel = ArticleModel(
                id: articleEntity.id,
                sourceId: articleEntity.sourceId,
                sourceName: articleEntity.sourceName,
                author: articleEntity.author,
                title: articleEntity.title,
                about: articleEntity.description,
                url: articleEntity.url,
                imageUrl: articleEntity.imageUrl,
                publishedAt: articleEntity.publishedAt,
                content: articleEntity.content,
                queries: [queryModel]
            )
            modelContext.insert(articleModel)
        }
    }
    
    func readArticles(_ query: String) -> [ArticleEntity] {
        let descriptor = FetchDescriptor<QueryModel>(
            predicate: #Predicate { $0.queryName == query }
        )

        return if let queryModel = try? modelContext.fetch(descriptor).first {
            queryModel.articles.map { articleModel in
                ArticleEntity(
                    id: articleModel.id,
                    sourceId: articleModel.sourceId,
                    sourceName: articleModel.sourceName,
                    author: articleModel.author,
                    title: articleModel.title,
                    description: articleModel.about,
                    url: articleModel.url,
                    imageUrl: articleModel.imageUrl,
                    publishedAt: articleModel.publishedAt,
                    content: articleModel.content
                )
            }
        } else {
            []
        }
    }
    
    func deleteArticles(_ query: String) throws {
        let descriptor = FetchDescriptor<QueryModel>(
            predicate: #Predicate { $0.queryName == query }
        )

        if let queryModel = try? modelContext.fetch(descriptor).first {
            modelContext.delete(queryModel)
            try modelContext.delete(model: ArticleModel.self, where: #Predicate {$0.queries.isEmpty})
        }
    }
    
    func readQueries() -> [QueryEntity] {
        let descriptor = FetchDescriptor<QueryModel>()

        let queryModels = try? modelContext.fetch(descriptor).map { queryModel in
            QueryEntity(queryName: queryModel.queryName)
        }

        return queryModels ?? []
    }

    private func fetchOrCreateQuery(_ queryName: String) throws -> QueryModel {
        let descriptor = FetchDescriptor<QueryModel>(
            predicate: #Predicate { $0.queryName == queryName }
        )

        if let existing = try modelContext.fetch(descriptor).first {
            return existing
        }

        let queryModel = QueryModel(queryName: queryName, articles: [])
        return queryModel
    }
}
