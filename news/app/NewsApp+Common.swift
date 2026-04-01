//
//  NewsApp+Common.swift
//  news
//
//  Created by Mendez, Juan on 4/1/26.
//

import Foundation
import SwiftUI

extension NewsApp {
    static var isUnitTesting: Bool {
        ProcessInfo.processInfo.environment["XCTestConfigurationFilePath"] != nil
    }

    static var isPreviewMode: Bool {
        ProcessInfo.processInfo.environment["XCODE_RUNNING_FOR_PREVIEWS"] == "1"
    }

    static var isUITesting: Bool {
        CommandLine.arguments.contains("--uitesting")
    }

    static var isRealApp: Bool {
        !isUnitTesting && !isPreviewMode && !isUITesting
    }
}
