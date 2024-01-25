//
//  iosApp.swift
//  ios
//
//  Created by Dennis Hills on 11/22/23.
//

import SwiftUI

@main
struct iosApp: App {
    var body: some Scene {
        WindowGroup {
            if #available(iOS 17.0, *) {
                ContentView()
            } else {
                // Fallback on earlier versions
            }
        }
    }
}
