//
//  CredentialManager.swift
//  PKBank
//
//  Created by Dennis Hills on 11/30/23.
//
//  Using SimpleKeychain tool from Auth0 here: https://github.com/auth0/SimpleKeychain
//  SimpleKeychain is being used to quickly/safely save and retrieve user tokens from the keychain
import Foundation
import AuthenticationServices
import SimpleKeychain

public class CredentialManager {
    
    var credentials: Credential?
    
    init (creds: Credential?) {
        self.credentials = creds
    }
    
    // Store the access and refresh tokens in the keychain
    func saveCredsLocal() -> Bool {
        
        let keychain = SimpleKeychain(service: "PKBank")
        guard let credentials else { return false }
        
        // Store the new tokens
        do {
            try keychain.set(credentials.id_token, forKey: "id_token")
            try keychain.set(credentials.access_token, forKey: "access_token")
            try keychain.set(credentials.refresh_token, forKey: "refresh_token")
            return true
        } catch {
            print("Error saving tokens to iOS Keychain: \(error)")
            return false
        }
    }
    
    // Store the preferred_username and userHandle in the keychain
    func saveUserInfoLocal(_ userInfo: UserInfo) -> Bool {
        let keychain = SimpleKeychain(service: "PKBank")
        
        do {
            try keychain.set(userInfo.preferred_username, forKey: "preferred_username")
            try keychain.set(userInfo.sub, forKey: "userHandle")
            return true
        } catch {
            print("Error saving username to iOS Keychain: \(error)")
            return false
        }
    }
    
    // Retrieve the id token from keychain
    func getIdTokenLocal() -> String? {
        let keychain = SimpleKeychain(service: "PKBank")
        
        do {
            let idToken = try keychain.string(forKey: "id_token")
            return idToken
        } catch {
            print("getIdTokenLocal(): Error retrieving Id token from iOS Keychain: \(error)")
            return ""
        }
    }
    
    // Retrieve the access token from keychain
    func getAccessTokenLocal() -> String? {
        let keychain = SimpleKeychain(service: "PKBank")
        
        do {
            let accessToken = try keychain.string(forKey: "access_token")
            return accessToken
        } catch {
            print("getAccessTokenLocal(): Error retrieving access token from iOS Keychain: \(error)")
            return ""
        }
    }
    
    // Retrieve the refresh token from keychain
    func getRefreshTokenLocal() -> String? {
        let keychain = SimpleKeychain(service: "PKBank")
        
        do {
            let refreshToken = try keychain.string(forKey: "refresh_token")
            return refreshToken
        } catch {
            print("Error retrieving refresh token from iOS Keychain: \(error)")
            return ""
        }
    }
    
    // Retrieve userHandl from keychain
    func getUserHandleLocal() -> String? {
        let keychain = SimpleKeychain(service: "PKBank")
        
        do {
            let userHandle = try keychain.string(forKey: "userHandle")
            print("Returning userHandle: \(userHandle)")
            return userHandle
        } catch {
            print("getUserHandleLocal(): Error retrieving userHandle from iOS Keychain: \(error)")
            return ""
        }
    }
    
    // Get user info from authorization server
    func getUserInfo() async -> String? {
        print("Getting user info from auth server...")
        var request = URLRequest(url: getURLEndpoint(endpoint: .userinfo)!)
        request.httpMethod = "POST"
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        request.setValue("Bearer \(getAccessTokenLocal()!)", forHTTPHeaderField: "Authorization")
        do {
            let (data, _) = try await URLSession.shared.data(for: request)
            
            data.printPrettyJSON("User info")
            
            do {
                let userInfoResponse = try JSONDecoder().decode(CredentialManager.UserInfo.self, from: data)
                if(saveUserInfoLocal(userInfoResponse)){
                    return userInfoResponse.preferred_username
                }
            } catch {
                print("decoding error:", error)
            }
        } catch _ as NSError {
            return "error"
        }
        return nil
    }
    
    // Exchange authorization code for access token
    func exchangeAuthorizationCodeForAccessToken(_ authorizationCode: String?) async -> Bool {
        
        let requestModel = OpenIDTokenRequest(
            grant_type: "authorization_code",
            client_id: "BankAppMobile",
            code: authorizationCode!,
            redirect_uri: "pkbank://callback"
        )
        
        guard let requestData: Data = try? URLEncodedFormEncoder().encode(requestModel) else {
            return false
        }

        var request = URLRequest(url: getURLEndpoint(endpoint: .token)!)
        request.httpBody = requestData
        request.httpMethod = "POST"
        request.addValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")

        do {
            let (data, _) = try await URLSession.shared.data(for: request)
            
            data.printPrettyJSON("Token response")
            
            do {
                let tokenResponse = try JSONDecoder().decode(CredentialManager.Credential.self, from: data)
                let credMgr = CredentialManager(creds: tokenResponse)
                if (credMgr.saveCredsLocal()){
                    let token = credMgr.getAccessTokenLocal()
                    print("Token stored in keychain: \(String(describing: token))")
                    
                    // Check for any pending transactions
//                    let queuedTransactions = TransactionQueueManager.shared.retrieveTransactions()
//                    if(!queuedTransactions.isEmpty){
//                        let trans = queuedTransactions[0]
//                        Task {
//                            let bankAPI = BankAPIManager()
//                            do {
//                                let bankResp = try await bankAPI.makeBankTransaction(transactionType: trans.transactionType, amount: trans.amount, desc: trans.desc)
//                            } catch  { }
//                        }
//                    }
                }
            } catch {
                print("decoding error:", error)
            }
            return true
        } catch _ as NSError {
            return false
        }
    }
    
    func renewAccessTokenWithRefreshToken() async -> Bool {
        print("renewAccessTokenWithRefreshToken(): AccessToken expired. Renewing token with refresh token...")
        let requestModel = OpenIDRefreshTokenRequest(
            grant_type: "refresh_token",
            client_id: "BankAppMobile",
            refresh_token: getRefreshTokenLocal(),
            redirect_uri: "pkbank://callback"
        )
        
        guard let requestData: Data = try? URLEncodedFormEncoder().encode(requestModel) else {
            return false
        }

        var request = URLRequest(url: getURLEndpoint(endpoint: .token)!)
        request.httpBody = requestData
        request.httpMethod = "POST"
        request.addValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")

        do {
            let (data, _) = try await URLSession.shared.data(for: request)
            
            data.printPrettyJSON("Token response")
            
            do {
                let tokenResponse = try JSONDecoder().decode(CredentialManager.Credential.self, from: data)
                let credMgr = CredentialManager(creds: tokenResponse)
                if (credMgr.saveCredsLocal()){
                    let token = credMgr.getAccessTokenLocal()
                    print("Newly refreshed Access Token stored in keychain: \(String(describing: token!))")
                    
                    // Check for any pending transactions
//                    let queuedTransactions = TransactionQueueManager.shared.retrieveTransactions()
//                    if(!queuedTransactions.isEmpty){
//                        let trans = queuedTransactions[0]
//                        Task {
//                            let bankAPI = BankAPIManager()
//                            do {
//                                let bankResp = try await bankAPI.makeBankTransaction(transactionType: trans.transactionType, amount: trans.amount, desc: trans.desc)
//                            } catch  { }
//                        }
//                    }
                }
            } catch {
                print("decoding error:", error)
            }
            return true
        } catch _ as NSError {
            return false
        }
    }
    
    func clearAllCredentials() {
        let keychain = SimpleKeychain(service: "PKBank")
        
        do {
            try keychain.deleteAll()
        } catch {
            print("Error deleting from Keychain: \(error)")
        }
    }
    
    func logOut() async -> Bool {
        var request = URLRequest(url: getURLEndpoint(endpoint: .logout)!)
        request.addValue("application/json", forHTTPHeaderField: "Accept")

        do {
            let (data, _) = try await URLSession.shared.data(for: request)
            clearAllCredentials()
            
        } catch _ as NSError {
            return false
        }
        return false
    }
    
    func getURLEndpoint(endpoint: Endpoint) -> URL? {
        switch endpoint {
            case .auth :
                return getAuthURL()
            case .token:
                return getTokenURL()
            case .userinfo:
                return getUserInfoURL()
            case .logout:
                return getLogOutURL()
        }
    }
    
    // Not really being used as we are using the preferred AWSWebAuthenticationSession
    func getAuthURL() -> URL {
        var components = URLComponents()
        components.scheme = "https"
        components.host = BANKAUTH.domain
        components.path = "/realms/BankApp/protocol/openid-connect/auth"
 
        return components.url!
    }
    
    func getTokenURL() -> URL {
        var components = URLComponents()
        components.scheme = "https"
        components.host = BANKAUTH.domain
        components.path = "/realms/BankApp/protocol/openid-connect/token"
 
        return components.url!
    }
    
    func getUserInfoURL() -> URL {
        var components = URLComponents()
        components.scheme = "https"
        components.host = BANKAUTH.domain
        components.path = "/realms/BankApp/protocol/openid-connect/userinfo"
 
        return components.url!
    }
    
    func getLogOutURL() -> URL {
        var components = URLComponents()
        components.scheme = "https"
        components.host = BANKAUTH.domain
        components.path = "/realms/BankApp/protocol/openid-connect/logout"
        components.queryItems = [ URLQueryItem(name: "id_token_hint", value: getIdTokenLocal())]
        return components.url!
    }
    
    struct OpenIDTokenRequest: Encodable {
        let grant_type: String
        let client_id: String
        let code: String?
        let redirect_uri: String
    }
    
    struct OpenIDRefreshTokenRequest: Encodable {
        let grant_type: String
        let client_id: String
        let refresh_token: String?
        let redirect_uri: String?
    }
    
    struct Credential : Decodable {
        let refresh_expires_in: Int
        let token_type: String
        let refresh_token: String
        let not_before_policy: Int
        let session_state: String
        let scope: String
        let access_token: String
        let id_token: String
        let expires_in: Int
        
        enum CodingKeys: String, CodingKey {
            case not_before_policy = "not-before-policy"
            case refresh_expires_in
            case token_type
            case refresh_token
            case session_state
            case scope
            case access_token
            case id_token
            case expires_in
        }
    }
    
    struct LogOutRequest: Encodable {
        let client_id: String
        let id_token_hint: String
        let redirect_uri: String
    }
    
    enum Endpoint {
        case auth
        case token
        case userinfo
        case logout
    }
    
    struct UserInfo: Decodable {
        let sub : String
        let email_verified: Bool
        let preferred_username: String
    }
}
