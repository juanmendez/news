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
        case debug
        case info
        case notice
        case warn
        case error
        case critical
        case preview
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
        attributes: Encodable? = nil,
    ) {
        guard Log.isEnabled else { return }

        let icon = icon(for: level)

        var logMessage = "\(icon) [\(level)] \(message)"

        if let error {
            logMessage += "\n\t❌ Error:\n\t\t\(error)"
        }

        if let attributes {
            if let jsonData = try? DecoderFactory.iso8601Encoder.encode(attributes),
                let jsonString = String(data: jsonData, encoding: .utf8) {
                logMessage += "\n\t📎 Attributes: \(jsonString)"
            } else {
                logMessage += "\n\t📎 Attributes: \(attributes)"
            }
        }

        print(logMessage)
    }

    private func icon(for level: LogLevel) -> String {
        switch level {
        case .debug:
            return "🔍"
        case .info:
            return "ℹ️"
        case .notice:
            return "📢"
        case .warn:
            return "⚠️"
        case .error:
            return "🚨"
        case .critical:
            return "💥"
        case .preview:
            return "👀"
        @unknown default:
            return "•"
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
        attributes: Encodable? = nil,
    ) {
        shared.log(message, error: error, level: .info, attributes: attributes)
    }

    public static func w(
        _ message: String,
        error: Error? = nil,
        attributes: Encodable? = nil,
    ) {
        shared.log(message, error: error, level: .warn, attributes: attributes)
    }

    public static func e(
        _ message: String,
        error: Error? = nil,
        attributes: Encodable? = nil,
    ) {
        shared.log(message, error: error, level: .error, attributes: attributes)
    }

    public static func p(
        _ message: String,
        attributes: Encodable? = nil,
    ) {
        shared.log(message, error: nil, level: .preview, attributes: attributes)
    }
}
