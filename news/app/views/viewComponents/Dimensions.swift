//
//  Dimensions.swift
//  news
//
//  Created by Mendez, Juan on 2/6/26.
//

import Foundation
import SwiftUI

/// Design system dimensions converted from Android resources
/// Original Android dimensions in dp (density-independent pixels) are converted to CGFloat for SwiftUI
struct Dimensions {

    // MARK: - Margins

    /// Standard margin size (20dp)
    static let margin: CGFloat = 20

    /// Small margin size (5dp)
    static let marginSmall: CGFloat = 5

    // MARK: - Card Layout

    /// Horizontal padding inside cards (20dp)
    static let cardPaddingHorizontal: CGFloat = 20

    /// Vertical padding inside cards (15dp)
    static let cardPaddingVertical: CGFloat = 15

    /// Vertical margin between cards (12dp)
    static let cardMarginVertical: CGFloat = 12

    /// Horizontal margin for cards (20dp)
    static let cardMarginHorizontal: CGFloat = 20

    /// Card elevation/shadow (10dp - use for shadow radius)
    static let cardElevation: CGFloat = 10

    // MARK: - Article Dimensions

    /// Height for article images (200dp)
    static let articleImageHeight: CGFloat = 200

    /// Font size for article source label (12sp)
    static let articleSourceTextSize: CGFloat = 12

    /// Font size for article title (18sp)
    static let articleTitleTextSize: CGFloat = 18

    /// Font size for article date (12sp)
    static let articleDateTextSize: CGFloat = 12

    /// Font size for article content (18sp)
    static let articleContentTextSize: CGFloat = 18

    // MARK: - Loading View

    /// Height for recycler loading view (80dp)
    static let listLoadingViewHeight: CGFloat = 80
}

// MARK: - Convenience Extensions

extension EdgeInsets {
    /// Create EdgeInsets with vertical and horizontal values
    /// - Parameters:
    ///   - vertical: Value for top and bottom
    ///   - horizontal: Value for leading and trailing
    init(vertical: CGFloat, horizontal: CGFloat) {
        self.init(
            top: vertical,
            leading: horizontal,
            bottom: vertical,
            trailing: horizontal
        )
    }
}

extension Dimensions {
    /// Edge insets for card padding
    static var cardPadding: EdgeInsets {
        EdgeInsets(
            vertical: cardPaddingVertical,
            horizontal: cardPaddingHorizontal
        )
    }

    /// Edge insets for card margins
    static var cardMargin: EdgeInsets {
        EdgeInsets(
            vertical: cardMarginVertical,
            horizontal: cardMarginHorizontal
        )
    }
}
