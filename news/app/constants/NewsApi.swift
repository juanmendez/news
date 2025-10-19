//
//  NewsApi.swift
//  news
//
//  Created by Mendez, Juan on 10/21/25.
//

import Foundation

enum NewsApi {
    // Xcode doesn't have the ability to manage secret keys as simple as it is done in Gradle.
    // So for simplicity, the API key is left in here, but git needs to ignore future changes.
    // git update-index --assume-unchanged news/app/constants/NewsApi.swift

    // to undo the ignore, run the following
    // git update-index --no-assume-unchanged news/app/constants/NewsApi.swift

    static let key = ""
}
