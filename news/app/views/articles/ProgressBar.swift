//
//  ProgressBar.swift
//  news
//
//  Created by Mendez, Juan on 2/7/26.
//

import SwiftUI

struct ProgressBar: View {

    var body: some View {
        HStack {
            ProgressView()
            Text("Loading more articles...")
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity)
        .padding()
    }
}


#Preview {
    List {
        ProgressBar()
    }
    .listStyle(.plain)
}
