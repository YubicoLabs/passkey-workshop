//
//  ContentView.swift
//  ios
//
//  Created by Dennis Hills on 11/22/23.
//
//  Make sure that Keycloak has a valid redirect URI as: pkbank://*
//
import SwiftUI

@available(iOS 17.0, *)
struct ContentView: View {
    @State private var isAuthenticated = false
    @State private var username: String = ""
    @State private var balance: String = ""
    
    var body: some View {

        if !isAuthenticated {
            PKBankLoginView(isAuthenticated: $isAuthenticated)
        } else {
            PKBankAccountView(isAuthenticated: $isAuthenticated)
        }
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
