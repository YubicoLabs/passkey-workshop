//
//  ContentView.swift
//  ios
//
//  Created by Dennis Hills on 11/22/23.
//
//  Make sure that Keycloak has a Valid redirect URI as: pkbank://*
//

import SwiftUI
import AuthenticationServices

class AuthenticationCoordinator: NSObject, ASWebAuthenticationPresentationContextProviding {
    func presentationAnchor(for session: ASWebAuthenticationSession) -> ASPresentationAnchor {
        // Return the window scene's key window as the presentation anchor
        return UIApplication.shared.windows.first!
    }
}

struct ContentView: View {
    @State private var isAuthenticated = false

    private let coordinator = AuthenticationCoordinator()
    
    var body: some View {
        VStack {
            if isAuthenticated {
                Text("Authentication Successful!")
            } else {
                Button("Authenticate") {
                    authenticate()
                }
                .padding()
            }
        }
    }
    
    func authenticate() {
        guard let authCodeURL = URL(string: "https://wc9g4jh8-8081.usw2.devtunnels.ms/realms/BankApp/protocol/openid-connect/auth?client_id=BankAppMobile&response_type=code") else {
            return
        }
        
        let authenticationSession = ASWebAuthenticationSession(url: authCodeURL, callbackURLScheme: "pkbank") { callbackURL, error in
            
            // Handle the authentication callback
            guard error == nil, let callbackURL = callbackURL else {
                print("Authentication failed with error: \(error?.localizedDescription ?? "Unknown error")")
                return
            }
            
            // Extract authorization "code" from the callback URL
            if let components = URLComponents(url: callbackURL, resolvingAgainstBaseURL: true),
               let queryItems = components.queryItems {
                for queryItem in queryItems {
                    if queryItem.name == "code" {
                        // Extract and handle the code as needed
                        print("Authentication successful. Code: \(queryItem.value ?? "")")
                        
                        Task {
                            do {
                                let tokenData = try await exchangeAuthorizationCodeForAccessToken(queryItem.value)
                            } catch {
                                print("Unexpected error retrieving access token: \(error.localizedDescription)")
                            }
                            isAuthenticated = true
                        }
                    }
                }
            }
        }
        authenticationSession.presentationContextProvider = coordinator
            
        // Start the authentication session
        authenticationSession.start()
        
        }
        
        func exchangeAuthorizationCodeForAccessToken(_ authorizationCode: String?) async -> Bool {
//            var components = URLComponents()
//            components.scheme = "https"
//            components.host = "wc9g4jh8-8081.usw2.devtunnels.ms"
//            components.path = "/realms/BankApp/protocol/openid-connect/token"
            
            guard let authTokenURL = URL(string: "https://wc9g4jh8-8081.usw2.devtunnels.ms/realms/BankApp/protocol/openid-connect/token") else {
                return false
            }
            
            let requestModel = OpenIDTokenRequest(
                grant_type: "authorization_code",
                client_id: "BankAppMobile",
                code: authorizationCode!,
                redirect_uri: "pkbank://"
            )
            guard let requestData: Data = try? URLEncodedFormEncoder().encode(requestModel) else {
                return false
            }

            var request = URLRequest(url: authTokenURL)
            request.httpBody = requestData
            request.httpMethod = "POST"
            request.addValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")
    
            do {
                let (data, _) = try await URLSession.shared.data(for: request)
                
                data.printPrettyJSON("Token response")
                
                do {
                    let tokenResponse = try JSONDecoder().decode(CredentialManager.Credential.self, from: data)
                    let credMgr = CredentialManager(creds: tokenResponse)
                    if(credMgr.saveCreds()){
                        let token = credMgr.getAccessToken()
                        print("Token stored in keychain: \(token)")
                    }
                }
                catch { 
                    print(error)
                }
                
                return true
            } catch _ as NSError {
                return false
            }
        }
    
    func getStoredAccessToken() -> String? {
        let creds = CredentialManager(creds: nil)
        return creds.getAccessToken()
    }
    
    struct AuthToken : Decodable {
        let refresh_expires_in: Int
        let token_type: String
        let refresh_token: String
        let not_before_policy: Int
        let session_state: String
        let scope: String
        let access_token: String
        let expires_in: Int
    }
    
//    enum CodingKeys: String, CodingKey {
//        case not_before_policy = "not-before-policy"
//        case refresh_expires_in
//        case token_type
//        case refresh_token
//        case session_state
//        case scope
//        case access_token
//        case expires_in
//    }
    
    struct OpenIDTokenRequest: Encodable {
        let grant_type: String
        let client_id: String
        let code: String
        let redirect_uri: String
    }
}

extension Data {
    
    func printPrettyJSON(_ str: String) {
        do {
            let json = try JSONSerialization.jsonObject(with: self, options: .mutableContainers)
            let jsonData = try JSONSerialization.data(withJSONObject: json, options: .prettyPrinted)
            printJSONData(str, jsonData)
        } catch {
            print("Error printing pretty JSON:\(error.localizedDescription)")
        }
    }
    
    private func printJSONData(_ str: String, _ data: Data) {
        print(str + ":\n" + String(decoding: data, as: UTF8.self) + "\n")
    }
}
