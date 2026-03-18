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
        InjectionProvider.register(
            DefaultInjections()
        )
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
