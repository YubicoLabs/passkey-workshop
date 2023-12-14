//
//  PKBankLoginView.swift
//  PKBank
//
//  Created by Dennis Hills on 12/13/23.
//
import SwiftUI
import AuthenticationServices

class AuthenticationCoordinator: NSObject, ASWebAuthenticationPresentationContextProviding {
    func presentationAnchor(for session: ASWebAuthenticationSession) -> ASPresentationAnchor {
        // Return the window scene's key window as the presentation anchor
        return UIApplication.shared.windows.first!
    }
}

private let coordinator = AuthenticationCoordinator()

struct PKBankLoginView: View {
    @Binding var isAuthenticated: Bool
    @State private var username: String = ""
    
    var body: some View {
        if isAuthenticated {
            PKBankAccountView(isAuthenticated: $isAuthenticated)
        } else {
//            Text("PK Bank Login")
//                .onAppear(perform: {
//                    authenticate()
//                })
        }
        VStack (alignment: .center, spacing: 30) {
                Text("Welcome to PKBS")
                    .font(Font.custom("Helvetica Neue", size: 30))
                    .foregroundColor(Color(red: 0.95, green: 0.94, blue: 1))
                VStack() {
                    Image(systemName: "person.circle.fill")
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                        .frame(width: 100, height: 100)
                        .foregroundColor(.white)
                        .padding()
                    Button(action: {
                        authenticate()
                    }) {
                        Text("Authenticate")
                            .font(Font.custom("Helvetica Neue", size: 25))
                            .foregroundColor(Color(red: 0.95, green: 0.94, blue: 1))
                            .padding(10)
                            .frame(maxWidth: .infinity)
                            .background(Color(red: 0.07, green: 0.27, blue: 1))
                            .cornerRadius(10)
                    }
                }
            }
            .padding(30)
            .frame(width: 376, alignment: .center)
            .background(Color(red: 0.23, green: 0.22, blue: 0.3))
            .cornerRadius(16)
            .shadow(color: .black.opacity(0.48), radius: 6, x: 2, y: 4)
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
                                
                                //await accountDetails()
                                
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

//#Preview {
//    PKBankLoginView(isAuthenticated: isAuthenticated = true)
//}
