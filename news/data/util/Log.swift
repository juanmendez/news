//
//  Log.swift
//  news
//
//  Created by Mendez, Juan on 9/16/25.
//

import Foundation

/// A simple logger utility for printing messages with different severity levels.
public final class Log {

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
    public static var isEnabled: Bool = true

    /// Logs a message with the specified log level.
    ///
    /// - Parameters:
    ///   - message: The message to log.
    ///   - level: The severity level of the log message.
    ///   - file: The file name where the log is called (auto-filled).
    private func log(
        _ message: String,
        level: LogLevel = .info,
        file: String = #file,
    ) {
        guard Log.isEnabled else { return }

        let fileName = (file as NSString).lastPathComponent
        let timestamp = Log.timestamp()

        print("[\(timestamp)] [\(level.rawValue)] [\(fileName) - \(message)")
    }

    /// Returns the current timestamp as a formatted string.
    private static func timestamp() -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd HH:mm:ss.SSS"
        return formatter.string(from: Date())
    }

    public static func i(
        _ file: String = #file,
        _ message: String,
    ) {
        shared.log(message, level: .info, file: file)
    }

    public static func w(
        _ file: String = #file,
        _ message: String,
    ) {
        shared.log(message, level: .warning, file: file)
    }

    public static func e(
        _ file: String = #file,
        _ message: String,
    ) {
        shared.log(message, level: .error, file: file)
    }
}
