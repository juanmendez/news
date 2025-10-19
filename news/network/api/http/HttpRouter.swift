//
//  HttpRouter.swift
//  news
//
//  Created by Mendez, Juan on 10/7/25.
//

import Foundation

protocol HttpRouter: Sendable {
    var path: String { get }
    var httpMethod: HTTPMethod { get }
}
