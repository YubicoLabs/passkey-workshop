//
//  CredentialManager.swift
//  PKBank
//
//  Created by Dennis Hills on 11/30/23.
//
//  Using SimpleKeychain tool from Auth0 here: https://github.com/auth0/SimpleKeychain
//

import Foundation
import SimpleKeychain

public class CredentialManager {
    
    var credentials: Credential?
    
    init (creds: Credential?) {
        self.credentials = creds
    }
    
    func saveCreds() -> Bool {
        
        let keychain = SimpleKeychain(service: "PKBank")
        guard let credentials else { return false }
        
        // Store the new tokens
        do {
            try keychain.set(credentials.access_token, forKey: "access_token")
            try keychain.set(credentials.refresh_token, forKey: "refresh_token")
            return true
        } catch {
            print("Error saving tokens to iOS Keychain: \(error)")
            return false
        }
    }
    
    func getAccessToken() -> String? {
        let keychain = SimpleKeychain(service: "PKBank")
        
        do {
            let accessToken = try keychain.string(forKey: "access_token")
            return accessToken
        } catch {
            print("Error retrieving access token from iOS Keychain: \(error)")
            return ""
        }
    }
    
    func getRefreshToken() -> String? {
        let keychain = SimpleKeychain(service: "PKBank")
        
        do {
            let refreshToken = try keychain.string(forKey: "refresh_token")
            return refreshToken
        } catch {
            print("Error retrieving refresh token from iOS Keychain: \(error)")
            return ""
        }
    }
    
    func getNewAccessTokenWithRefreshToken() {
        
    }
    
    struct Credential : Decodable {
        let refresh_expires_in: Int
        let token_type: String
        let refresh_token: String
        let not_before_policy: Int
        let session_state: String
        let scope: String
        let access_token: String
        let expires_in: Int
        
        enum CodingKeys: String, CodingKey {
            case not_before_policy = "not-before-policy"
            case refresh_expires_in
            case token_type
            case refresh_token
            case session_state
            case scope
            case access_token
            case expires_in
        }
    }
}
