//
//  newsApp.swift
//  news
//
//  Created by Mendez, Juan on 9/16/25.
//

import SwiftData
import SwiftUI

@main
struct NewsApp: App {
    let container: ModelContainer

    init() {
        do {
            container = try ModelContainer(
                for: ArticleModel.self,
                configurations: ModelConfiguration(isStoredInMemoryOnly: !NewsApp.isRealApp)
            )
        } catch {
            fatalError("Failed to create ModelContainer: \(error)")
        }

        if NewsApp.isPreviewMode {
            InjectionProvider.register(PreviewInjections(modelContext: container.mainContext))
        } else {
            InjectionProvider.register(DefaultInjections(modelContext: container.mainContext))

        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
        .modelContainer(container)
    }
}
