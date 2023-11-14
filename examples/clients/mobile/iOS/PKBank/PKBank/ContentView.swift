//
//  ContentView.swift
//  PKBank
//
//  Created by Dennis Hills on 11/13/23.
//

import SwiftUI

var webAuthenticationSession: WebAuthenticationSession { get }

struct ContentView: View {
    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundStyle(.tint)
            Text("Hello, world!")
        }
        .padding()
    }
}

struct WebAuthenticationSessionExample: View {
    @Environment(\.authorizationController) private var authorizationController

    var body: some View {
        Button("Sign In") {
            Task {
                do {
                    let urlWithToken = try await webAuthenticationSession.authenticate(
                        using: URL(string: "https://www.example.com")!,
                        callbackURLScheme: "x-example-app")
                    try await signIn(using: urlWithToken) // defined elsewhere
                } catch {
                    // code to handle authentication errors
                }
            }
        }
    }
}

#Preview {
    ContentView()
}
