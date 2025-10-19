//
//  ContentView.swift
//  news
//
//  Created by Mendez, Juan on 9/16/25.
//

import SwiftUI

struct ContentView: View {
    @Inject var repository: Repository

    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundStyle(.tint)
            Text("Hello, world!")
        }
        .padding()
        .onAppear {
            Task {
                for await value in repository.getArticles(query: "", page: 1) {
                    print("Received value: \(value)")
                }
            }
        }
    }
}

#Preview {
    ContentView()
}
