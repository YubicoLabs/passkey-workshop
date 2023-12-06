//
//  ContentView.swift
//  ios
//
//  Created by Dennis Hills on 11/22/23.
//
//  Make sure that Keycloak has a valid redirect URI as: pkbank://*
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
        
        // Either specify URLScheme in the Info.plist or here. nil is setting the URLScheme from plist
        let authenticationSession = ASWebAuthenticationSession(url: getAuthURL(), callbackURLScheme: nil) { callbackURL, error in
            
            // Handle the authentication callback
            guard error == nil, let callbackURL = callbackURL else {
                print("Authentication failed with error: \(error?.localizedDescription ?? "Unknown error")")
                return
            }
            
            // Extract auth "code" from the OpenID callback URL
            if let components = URLComponents(url: callbackURL, resolvingAgainstBaseURL: true),
               let queryItems = components.queryItems {
                for queryItem in queryItems {
                    if queryItem.name == "code" {
                        // Extract code from redirect
                        print("Authentication successful.\nAuth Code: \(queryItem.value ?? "")")
                        let authCode = queryItem.value
                        
                        // Requesting access token from Keycloak in exchange for code
                        Task {
                            do {
                                if (try await CredentialManager(creds: nil).exchangeAuthorizationCodeForAccessToken(authCode)) {
                                    isAuthenticated = true
                                    
                                    let username = try await CredentialManager(creds: nil).getUserInfo()
                                    print("Welcome,\(username!)")
                                } else {
                                    print("Failed to exchange code for access token")
                                    isAuthenticated = false
                                }
                            } catch {
                                print("Unexpected error retrieving access token: \(error.localizedDescription)")
                            }
                        }
                    } else {
                        print("No auth code found in callback from OpenID provider")
                    }
                }
            }
        }
        authenticationSession.presentationContextProvider = coordinator
            
        // Start the authentication session
        authenticationSession.start()
    }
    
    func getStoredAccessToken() -> String? {
        let creds = CredentialManager(creds: nil)
        return creds.getAccessToken()
    }
    
    // WORKING for openid on mobile
    func getAuthURL() -> URL {
        var components = URLComponents()
        components.scheme = "https"
        components.host = "wc9g4jh8-8081.usw2.devtunnels.ms"
        components.path = "/realms/BankApp/protocol/openid-connect/auth"
        
        components.queryItems =
            [
                URLQueryItem(name: "client_id", value: "BankAppMobile"),
                URLQueryItem(name: "redirect_uri", value: "pkbank://callback"),
                URLQueryItem(name: "scope", value: "openid"),
                URLQueryItem(name: "response_type", value: "code"),
                URLQueryItem(name: "state", value: "standard")
            ]
        return components.url!
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
