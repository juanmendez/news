//
//  String+Common.swift
//  news
//
//  Created by Mendez, Juan on 11/17/25.
//


import Foundation
import SwiftUI

extension String {
    /// Returns true when the string contains at least one character.
    ///
    /// Equivalent to `!isEmpty` and provided for readability in conditionals.
    ///
    /// ### Example:
    /// ```swift
    /// "Hello".isNotEmpty  // true
    /// "".isNotEmpty       // false
    /// ```
    nonisolated var isNotEmpty: Bool {
        isEmpty == false
    }

    /// Returns true if the string is empty OR only contains whitespace/newline characters.
    ///
    /// Useful for validating text fields where pure whitespace should be treated as empty.
    ///
    /// ### Example:
    /// ```swift
    /// "".isBlank                // true
    /// "    \n".isBlank          // true
    /// "  text  ".isBlank         // false
    /// ```
    nonisolated var isBlank: Bool {
        isEmpty == true || trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }

    /// The inverse of `isBlank`. Returns true when the string has at least one non-whitespace character.
    ///
    /// ### Example:
    /// ```swift
    /// "  text  ".isNotBlank   // true
    /// "   \n".isNotBlank     // false
    /// ```
    nonisolated var isNotBlank: Bool {
        !isBlank
    }

    /// Compares the current string with another string for equality, with no case sensitivity.
    ///
    /// - Parameters:
    ///   - stringToCompare: The string to compare against the current string.
    ///   - Returns: `true` if the strings are equal without case sensitivity; otherwise, `false`.
    func equalsNoCase(_ stringToCompare: String) -> Bool {
        self.lowercased() == stringToCompare.lowercased()
    }

    /// Returns a new string made by removing both leading and trailing whitespace and newline characters.
    ///
    /// - Returns: A trimmed version of the original string.
    ///
    /// ### Example:
    /// ```swift
    /// let blankString = "    "
    /// print(blankString.trim()) // ""
    ///
    /// let rawString = "\n   Hello, Swift!   \n"
    /// print(rawString.trimmed) // "Hello, Swift!"
    /// ```
    func trim() -> String {
        trimmingCharacters(in: .whitespacesAndNewlines)
    }

    /// Provides a displayable string for an optional quantity.
    ///
    /// - Parameter quantity: An optional integer value.
    /// - Returns: The integer converted to a string when non-nil; otherwise an em dash ("—") placeholder.
    ///
    /// ### Example:
    /// ```swift
    /// String.displayQty(quantity: 5)    // "5"
    /// String.displayQty(quantity: nil)  // "—"
    /// ```
    static func displayQty(quantity: Int?) -> String {
        if let quantity {
            return "\(quantity)"
        } else {
            return "—"
        }
    }

}

extension String? {
    /// Returns true when the optional string is non-nil and contains at least one non-whitespace character.
    ///
    /// Combines a nil check with `String.isNotBlank` for concise optional content validation.
    ///
    /// ### Example:
    /// ```swift
    /// let value: String? = "Hello"
    /// value.hasContent        // true
    /// let spaces: String? = "   "
    /// spaces.hasContent       // false
    /// let none: String? = nil
    /// none.hasContent         // false
    /// ```
    nonisolated var hasContent: Bool {
        if let self {
            self.isNotBlank
        } else {
            false
        }
    }
}
