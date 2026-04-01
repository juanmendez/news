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
        if NewsApp.isRealApp {
            InjectionProvider.register(DefaultInjections())
        } else {
            InjectionProvider.register(PreviewInjections())
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
