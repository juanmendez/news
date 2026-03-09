//
//  Log.swift
//  news
//
//  Created by Mendez, Juan on 9/16/25.
//

import Foundation

/// A simple logger utility for printing messages with different severity levels.
public final class Log: Sendable {

    /// Defines the severity level of a log message.
    public enum LogLevel: String {
        case debug = "DEBUG"
        case info = "INFO"
        case warning = "WARNING"
        case error = "ERROR"
    }

    /// Shared singleton instance for global logging.
    private static let shared = Log()

    /// Controls whether logging is enabled.
    public nonisolated(unsafe) static var isEnabled: Bool = true

    /// Logs a message with the specified log level.
    ///
    /// - Parameters:
    ///   - message: The message to log.
    ///   - error: The error content to log.
    ///   - level: The severity level of the log message.

    private func log(
        _ message: String,
        error: Error? = nil,
        level: LogLevel,
    ) {
        guard Log.isEnabled else { return }

        let timestamp = Log.timestamp()

        print("[\(timestamp)] [\(level.rawValue)] - \(message)")

        if let error {
            print("[\(timestamp)] [\(level.rawValue)] - \(error.localizedDescription)")
        }
    }

    private static func timestamp() -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd HH:mm:ss.SSS"
        return formatter.string(from: Date())
    }

    public static func i(
        _ message: String,
        error: Error? = nil,
    ) {
        shared.log(message, error: error, level: .info)
    }

    public static func w(
        _ message: String,
        error: Error? = nil,
    ) {
        shared.log(message, error: error, level: .warning)
    }

    public static func e(
        _ message: String,
        error: Error? = nil,
    ) {
        shared.log(message, error: error, level: .error)
    }
}
