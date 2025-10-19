//
//  DefaultHttpRouter.swift
//  news
//
//  Created by Mendez, Juan on 10/13/25.
//

import Foundation

enum DefaultHttpRouter: HttpRouter {
    case newsByPage

    var httpMethod: HTTPMethod {
        switch self {
            case .newsByPage:
                return .get
        }
    }

    var path: String {
        switch self {
            case .newsByPage:
                return "/v2/everything"
        }
    }
}
