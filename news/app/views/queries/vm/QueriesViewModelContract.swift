//
//  QueriesViewModelContract.swift
//  news
//
//  Created by Mendez, Juan on 4/13/26.
//

import Foundation

protocol QueriesViewModelContract {
    var queries: [QueryEntity] { get }
    func refreshQueries() async
}
