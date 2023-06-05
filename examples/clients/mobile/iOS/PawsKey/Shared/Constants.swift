//
//  Constants.swift
//  PawsKey
//
//  Created by Dennis Hills on 6/5/23.
//  Copyright © 2023 Yubico. All rights reserved.
//
import Foundation

enum Constants {
    enum Error: Swift.Error {
        case missingKey, invalidValue
    }

    static func value<T>(for key: String) throws -> T where T: LosslessStringConvertible {
        guard let object = Bundle.main.object(forInfoDictionaryKey:key) else {
            throw Error.missingKey
        }

        switch object {
        case let value as T:
            return value
        case let string as String:
            guard let value = T(string) else { fallthrough }
            return value
        default:
            throw Error.invalidValue
        }
    }
}

enum API {
    static var baseURL: String {
        return try! "https://" + Constants.value(for: "API_BASE_URL")
    }
}

enum RP {
    static var ID: String {
        return try! Constants.value(for: "RP_ID")
    }
}
