//
//  HttpClientResponseRaw.swift
//  news
//
//  Created by Mendez, Juan on 10/7/25.
//

import Foundation

struct HttpClientResponseRaw {
    var data: Data
    var response: HTTPURLResponse

    init(_ tuple: (data: Data, response: HTTPURLResponse)) {
        self.data = tuple.data
        self.response = tuple.response
    }
}
