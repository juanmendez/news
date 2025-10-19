//
//  DefaultJsonDecoder.swift
//  news
//
//  Created by Mendez, Juan on 10/13/25.
//

import Foundation

enum DecoderFactory {
    static var iso8601Decoder: JSONDecoder {
        let decoder = JSONDecoder()
        decoder.dateDecodingStrategy = .iso8601
        return decoder
    }
}
