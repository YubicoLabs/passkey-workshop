//
//  Constants.swift
//  PawsKey
//
//  Created by Dennis Hills on 5/17/24.
//  Copyright © 2024 Yubico. All rights reserved.
//
//  See Configuration/Constants.xcconfig for API_BASE_URI/RP_ID values
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

enum APIBASEURI {
    static var domain: String {
        return try! Constants.value(for: "API_BASE_URI")
    }
}

enum RPID {
    static var domain: String {
        return try! Constants.value(for: "RP_ID")
    }
}
