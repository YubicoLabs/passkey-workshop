//
//  Constants.swift
//  PKBank
//
//  Created by Dennis Hills on 12/1/23.
//
//  See Constants.xcconfig for Bank Auth/API values

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

enum BANKAUTH {
    static var domain: String {
        return try! Constants.value(for: "BANK_AUTH_DOMAIN")
    }
}

enum BANKAPI {
    static var domain: String {
        return try! Constants.value(for: "BANK_API_DOMAIN")
    }
}
