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
            Button("Authenticate") {
                authenticate()
            }
            .padding()
            Button("Make Transaction") {
                Task {
                    let bankAPI = BankAPIManager()
                    try await bankAPI.makeBankTransaction(transactionType: BankTransactionType.deposit, amount: 1001.00, desc: "iron")
                }
            }
            .padding()
            Button("Get Transactions") {
                Task {
                    let bankAPI = BankAPIManager()
                    try await bankAPI.getBankTransactions()
                }
            }
            .padding()
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
                        print("Authentication successful.\nAuth Code -> \(queryItem.value ?? "")")
                        let authCode = queryItem.value
                        
                        // Requesting access token from Keycloak in exchange for code
                        Task {
                            do {
                                if (try await CredentialManager(creds: nil).exchangeAuthorizationCodeForAccessToken(authCode)) {
                                    isAuthenticated = true
                                    
                                    //let username = try await CredentialManager(creds: nil).getUserInfo()
                                    
                                } else {
                                    print("Failed to exchange code for access token")
                                    isAuthenticated = false
                                }
                                
                                await accountDetails()
                                
                            } catch {
                                print("Unexpected error retrieving access token: \(error.localizedDescription)")
                            }
                        }
                    }
                }
            }
        }
        authenticationSession.presentationContextProvider = coordinator
            
        // Start the authentication session
        authenticationSession.start()
    }
    
    func accountDetails() async {
        do {
            let username = try await CredentialManager(creds: nil).getUserInfo()
            
            let bankAPI = BankAPIManager()
            let accountDetails = try await bankAPI.fetchAccountsDetails()
            print("Welcome, \(username!). Your account balance is: $\(accountDetails.accounts[0].balance!)")
        } catch {
            
        }
    }
    
    func getStoredAccessToken() -> String? {
        let creds = CredentialManager(creds: nil)
        return creds.getAccessTokenLocal()
    }
    
    // WORKING for openid on mobile
    func getAuthURL() -> URL {
        var components = URLComponents()
        components.scheme = "https"
        components.host = BANKAUTH.domain
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
            print("Error printing pretty JSON:\(error)")
        }
    }
    
    private func printJSONData(_ str: String, _ data: Data) {
        print(str + ":\n" + String(decoding: data, as: UTF8.self) + "\n")
    }
}
