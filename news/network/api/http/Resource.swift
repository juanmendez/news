//
//  Resource.swift
//  news
//
//  Created by Mendez, Juan on 10/4/25.
//

import Foundation

/// Resource wrapper that adds state and error message to data.
///
/// This is done in order to bundle the loading state and an error message with the data. This
/// relieves the upper layers (ViewModel) from the responsibility of managing the data state such
/// as the loading state (and its associated progress indicator), the error state (and its error
/// dialog), and finally the nominal success state. The Repository will first emit a Resource to
/// indicate the loading state, later it will emit another Resource once the data is retrieved,
/// and eventually a different Resource in case of an error.
///
/// The data and message are wrapped in a consumable [Event] as they will be consumed by the UI and
/// should not be showed again. For example if the Airplane mode is set to ON, the UI will receive
/// and display an error message. If the phone changes orientations that error message will be
/// displayed again (LiveData) unless wrapped into a consumable [Event].
///
/// The wrapped data is usually the ViewState ([ArticleListViewState] for example)
///
/// @param T the data type
/// @param status the [Status] of the [Resource]
/// @param data the data wrapped into a consumable [Event]
/// @param message the message [String] wrapped into a consumable [Event]
enum Resource<Item: Equatable & Codable>: Equatable {
    case loading(item: Item? = nil)
    case success(item: Item)
    case error(error: Error)

    static func == (lhs: Self, rhs: Self) -> Bool {
        switch (lhs, rhs) {
        case (.loading(let lhsItem), .loading(let rhsItem)):
            return lhsItem == rhsItem
        case (.success(let lhsItem), .success(let rhsItem)):
            return lhsItem == rhsItem
        case (.error(let lhsError), .error(let rhsError)):
            return "\(lhsError)" == "\(rhsError)"
        default:
            return false
        }
    }
}

extension Resource {
    var isLoading: Bool {
        if case .loading(_) = self {
            true
        } else {
            false
        }
    }
}
