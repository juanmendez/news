//
//  DecoderFactory.swift
//  news
//
//  Created by Mendez, Juan on 10/13/25.
//

import Foundation

enum DecoderFactory {
    nonisolated(unsafe) static var iso8601Decoder: JSONDecoder {
        let decoder = JSONDecoder()
        decoder.dateDecodingStrategy = .iso8601
        return decoder
    }
    
    nonisolated(unsafe) static var iso8601Encoder: JSONEncoder {
        let encoder = JSONEncoder()
        encoder.dateEncodingStrategy = .iso8601
        encoder.outputFormatting = .prettyPrinted
        return encoder
    }
}
