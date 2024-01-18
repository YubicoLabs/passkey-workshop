//
//  PKBankView.swift
//  PKBank
//
//  Created by Dennis Hills on 12/13/23.
//
import SwiftUI

struct PKBankAccountView: View {
    @Binding var isAuthenticated: Bool
    @State private var shouldPresentStepUp = false
    @State private var username: String = ""
    @State private var balance: Double = 0.00
   
    var body: some View {
        Button(""){
            
        }
        .confirmationDialog("STEP UP REQUIRED", isPresented: $shouldPresentStepUp) {
            Button("UNLOCK WITH SECURITY KEY") {
                PKBankLoginView(isAuthenticated: $isAuthenticated).authenticate()
            }
        }
        if !isAuthenticated {
            PKBankLoginView(isAuthenticated: $isAuthenticated)
        }
        Text("PK Bank Account")
            .onAppear(perform: {
                Task {
                    if isAuthenticated {
                        await getBankAccountDetails()
                    }
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
           Button("Deposit $100") {
                Task {
                    let bankAPI = BankAPIManager()
                    let bankResp = try await bankAPI.makeBankTransaction(transactionType: BankTransactionType.deposit, amount: 100.00, desc: "deposit $100.00")
                    print("Bank transaction response: \(bankResp)")
                    await getBankAccountDetails()
                }
            }
            .font(Font.custom("Helvetica Neue", size: 15))
            .foregroundColor(Color(red: 0.95, green: 0.94, blue: 1))
            .padding()
            .frame(maxWidth: .infinity)
            .background(Color(red: 0.07, green: 0.27, blue: 1))
            .cornerRadius(10)
            .padding()
            
            Button("Withdraw $50") {
                Task {
                    let bankAPI = BankAPIManager()
                    let bankResp = try await bankAPI.makeBankTransaction(transactionType: BankTransactionType.withdraw, amount: 50.00, desc: "autodraft $50.00")
                    print("Bank transaction response: \(bankResp)")
                    await getBankAccountDetails()
                }
            }
            .font(Font.custom("Helvetica Neue", size: 15))
            .foregroundColor(Color(red: 0.95, green: 0.94, blue: 1))
            .padding()
            .frame(maxWidth: .infinity)
            .background(Color(red: 0.07, green: 0.27, blue: 1))
            .cornerRadius(10)
            .padding()
            
            Button("Withdraw $1,200") {
                Task {
                    let bankAPI = BankAPIManager()
                    do {
                        let bankResp = try await bankAPI.makeBankTransaction(transactionType: BankTransactionType.withdraw, amount: 1200.00, desc: "autodraft $1200.00")
                    } catch  {
                        if(error as! BankAPIError == BankAPIError.requestFailed) {
                            shouldPresentStepUp.toggle()
                        }
                    }
                    
                    await getBankAccountDetails()
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
                Task {
                    await signOut()
                }
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
    
    var stepUpView: some View {
        VStack (alignment: .leading){
            Text("Here is a list of new features:")
            Text("Hello World")
            Image(systemName: "plus")
        }
    }
    
    func getBankAccountDetails() async {
        
        let username = await CredentialManager(creds: nil).getUserInfo()
        
        do {
            let bankAPI = BankAPIManager()
            let accountDetails = try await bankAPI.fetchAccountsDetails()
            
            self.username = username!
            if(!accountDetails.accounts.isEmpty) {
                self.balance = accountDetails.accounts[0].balance!
            }
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
    
    func signOut() async {
        await CredentialManager(creds: nil).logOut()
        isAuthenticated = false
    }
}
