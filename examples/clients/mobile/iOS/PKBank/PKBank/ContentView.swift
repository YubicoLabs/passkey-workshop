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
        guard let authCodeURL = URL(string: "https://wc9g4jh8-8081.usw2.devtunnels.ms/realms/BankApp/protocol/openid-connect/auth?client_id=BankApp&response_type=code") else {
            return
        }
        
        let authenticationSession = ASWebAuthenticationSession(url: authCodeURL, callbackURLScheme: "pkbank") { callbackURL, error in
            // Handle the authentication callback
            guard error == nil, let callbackURL = callbackURL else {
                print("Authentication failed with error: \(error?.localizedDescription ?? "Unknown error")")
                return
            }
            
            // Extract authentication information from the callback URL
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
                                print("Unexpected error: \(error.localizedDescription)")
                            }
                            isAuthenticated = true
                        }
                    }
                }
                print("Authentication failed. Unable to extract token.")
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
                client_id: "BankApp",
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
            print(request)
            do {
                // Using URLSession.data(for:) to make the request and retrieve the data
                let (data, _) = try await URLSession.shared.data(for: request)
                let responseString = String(data: data, encoding: .utf8)
                print("Token Response: \(responseString ?? "")")
                return true
            } catch _ as NSError {
                return false
            }
        }
    
    struct AuthToken : Decodable {
        let access_token: String
        let expires_in: Int
        let refresh_expires_in: Int
        let refresh_token: String
        let token_type: String
        let session_state: String
        let scope: String
    }
    
    struct OpenIDTokenRequest: Encodable {
        let grant_type: String
        let client_id: String
        let code: String
        let redirect_uri: String
    }
}
