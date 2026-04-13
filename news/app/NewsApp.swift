//
//  newsApp.swift
//  news
//
//  Created by Mendez, Juan on 9/16/25.
//

import SwiftUI

@main
struct NewsApp: App {
    init() {
        if NewsApp.isPreviewMode {
            InjectionProvider.register(PreviewInjections())
        } else {
            InjectionProvider.register(DefaultInjections())
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
