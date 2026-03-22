//
//  Array+Comon.swift
//  news
//
//  Created by Mendez, Juan on 11/17/25.
//


extension Array {
    /// Returns `true` if the array contains at least one element, otherwise `false`.
    nonisolated var isNotEmpty: Bool {
        !isEmpty
    }

    /// Returns a sub-array starting at `from` index, containing up to `count` elements.
    ///
    /// - If `count` exceeds the number of remaining elements, only the available
    ///   elements from `from` to the end of the array are returned.
    /// - If `from` is at or beyond the last index, an empty array is returned.
    ///
    /// ### Example:
    /// ```swift
    /// articles.slice(0, 2) // articles[0..<2]
    /// articles.slice(2, 2) // articles[2..<4]
    /// articles.slice(0, 100) // all elements when count exceeds array size
    /// articles.slice(8, 2)   // [] when from is at or beyond the last index
    /// ```
    ///
    /// - Parameters:
    ///   - from: The zero-based start index.
    ///   - count: The maximum number of elements to include.
    /// - Returns: An `Array` with up to `count` elements starting at `from`.
    func slice(_ from: Int, _ count: Int) -> [Element] {
        Array(dropFirst(from).prefix(count))
    }
}
