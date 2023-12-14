//
//  PKBankView.swift
//  PKBank
//
//  Created by Dennis Hills on 12/13/23.
//
import SwiftUI

struct PKBankAccountView: View {
    @Binding var isAuthenticated: Bool
    @State private var username: String = ""
    @State private var balance: Double = 0.00
   
    var body: some View {
        if !isAuthenticated {
            PKBankLoginView(isAuthenticated: $isAuthenticated)
        }
        Text("PK Bank Account")
            .onAppear(perform: {
                Task {
                    try await getBankAccountDetails()
                }
            })
            .font(Font.custom("Helvetica Neue", size: 40))
            .padding(10)
        Text("Welcome, \(username)")
            .font(Font.custom("Helvetica Neue", size: 25))
            .padding(5)
        Text("Your balance is: \(formatBalanceToCurrency(amount: balance))")
            .font(Font.custom("Helvetica Neue", size: 25))
            .padding(5)
        VStack {
            Button("Withdraw $50.00") {
                Task {
                    let bankAPI = BankAPIManager()
                    try await bankAPI.makeBankTransaction(transactionType: BankTransactionType.withdraw, amount: 50.00, desc: "autodraft $50.00")
                    try await getBankAccountDetails()
                }
            }
            .font(Font.custom("Helvetica Neue", size: 15))
            .foregroundColor(Color(red: 0.95, green: 0.94, blue: 1))
            .padding()
            .frame(maxWidth: .infinity)
            .background(Color(red: 0.07, green: 0.27, blue: 1))
            .cornerRadius(10)
            .padding()
            
            Button("Withdraw $1,000.00") {
                Task {
                    let bankAPI = BankAPIManager()
                    try await bankAPI.makeBankTransaction(transactionType: BankTransactionType.withdraw, amount: 1000.00, desc: "autodraft $1000.00")
                    try await getBankAccountDetails()
                }
            }
            .font(Font.custom("Helvetica Neue", size: 15))
            .foregroundColor(Color(red: 0.95, green: 0.94, blue: 1))
            .padding()
            .frame(maxWidth: .infinity)
            .background(Color(red: 0.07, green: 0.27, blue: 1))
            .cornerRadius(10)
            .padding()
                       
            Button("Sign Out") {
                signOut()
            }
            .font(Font.custom("Helvetica Neue", size: 25))
            .foregroundColor(Color(red: 0.95, green: 0.94, blue: 1))
            .padding(10)
            .frame(maxWidth: .infinity)
            .background(Color.red)
            .cornerRadius(10)
            .padding()
        }
    }
    
    func getBankAccountDetails() async {
        do {
            let username = try await CredentialManager(creds: nil).getUserInfo()
            
            let bankAPI = BankAPIManager()
            let accountDetails = try await bankAPI.fetchAccountsDetails()
            
            self.username = username!
            self.balance = accountDetails.accounts[0].balance!
            
        } catch {
            
        }
    }
    
    private func formatBalanceToCurrency(amount: Double) -> String {
        let numberFormatter = NumberFormatter()
        numberFormatter.numberStyle = .currency
        numberFormatter.groupingSeparator = ","
        let formattedNumber = numberFormatter.string(from: NSNumber(value: amount))
        return formattedNumber ?? ""
    }
    
    func signOut() {
        CredentialManager(creds: nil).clearAllCredentials()
        isAuthenticated = false
    }
}
