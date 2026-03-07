//
//  ArticlesToolbarModifier.swift
//  news
//
//  Created by Mendez, Juan on 3/6/26.
//

import SwiftUI

struct ArticlesToolbarModifier: ViewModifier {
    @Binding var query: String
    let onRefresh: () async -> Void
    let onSubmit: () async -> Void

    func body(content: Content) -> some View {
        content
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button {
                        Task {
                            await onRefresh()
                        }
                    } label: {
                        Image(systemName: "arrow.clockwise")
                    }
                }
            }
            .searchable(
                text: $query,
                placement: .navigationBarDrawer(displayMode: .always),
                prompt: "search_hint"
            )
            .onSubmit(of: .search) {
                Task {
                    await onSubmit()
                }
            }
    }
}

extension View {
    func articlesToolbar(
        query: Binding<String>,
        onRefresh: @escaping () async -> Void,
        onSubmit: @escaping () async -> Void
    ) -> some View {
        modifier(ArticlesToolbarModifier(query: query, onRefresh: onRefresh, onSubmit: onSubmit))
    }
}
