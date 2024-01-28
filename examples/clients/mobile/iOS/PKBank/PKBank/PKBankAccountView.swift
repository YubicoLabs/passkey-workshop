//
//  PKBankView.swift
//  PKBank
//
//  Created by Dennis Hills on 12/13/23.
//
import SwiftUI

@available(iOS 17.0, *)
struct PKBankAccountView: View {
    @Binding var isAuthenticated: Bool
    @State private var shouldPresentStepUp = false
    @State private var username: String = ""
    @State private var balance: Double = 0.00
    @State private var isTransactionPending = false
   
    var body: some View {
        Button(""){
        }
        .confirmationDialog("STEP UP REQUIRED", isPresented: $shouldPresentStepUp) {
            Button("UNLOCK WITH SECURITY KEY") {
                // Call step-up authentication
                PKBankLoginView(isAuthenticated: $isAuthenticated).authenticate(action_type: PKBankLoginView.ActionType.STEPUP, username)
            }
        }
        if !isAuthenticated {
            PKBankLoginView(isAuthenticated: $isAuthenticated)
        } 
        Text("PK Bank Account")
            .onAppear(perform: {
                Task {
                    if isAuthenticated {
                       let accountDetails = await getBankAccountDetails()
                        if(accountDetails.accounts.isEmpty){
                            await getBankAccountDetails()
                        } else {
                            if(isTransactionPending){
                                print("Transactions pending...retrying transactions from queue")
                                let transQueue = TransactionQueueManager.shared.retrieveTransactions()
                                await bankTransaction(transQueue[0])
                            } else {
                                print("No transactions pending...continue")
                            }
                        }
                    }
                }
            })
            .onChange(of: isTransactionPending, initial: isTransactionPending){
                print("isTransactionPending STATE changed")
            }
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
                   let trans = Transaction(transactionType: BankTransactionType.deposit, amount: 100.00, desc: "deposit of $100.00")
                   await bankTransaction(trans)
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
                    let trans = Transaction(transactionType: BankTransactionType.withdraw, amount: 50.00, desc: "autodraft $50.00")
                    await bankTransaction(trans)
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
                    let trans = Transaction(transactionType: BankTransactionType.withdraw, amount: 1200.00, desc: "autodraft $1200.00")
                    await bankTransaction(trans)
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
    
    func bankTransaction(_ transaction: Transaction) async {
        Task {
            let bankAPI = BankAPIManager()
            do {
                let bankResp = try await bankAPI.makeBankTransaction(transactionType: transaction.transactionType, amount: transaction.amount, desc: transaction.desc)
                // Clear all transactions
                TransactionQueueManager.shared.clearTransactions()
                isTransactionPending = false
            } catch  {
                if(error as! BankAPIError == BankAPIError.requestFailed) {
                    let trans = Transaction(transactionType: transaction.transactionType, amount: transaction.amount, desc: transaction.desc)
                    TransactionQueueManager.shared.addTransaction(trans)
                    isTransactionPending = true
                    if(transaction.amount >= 1000.00){
                        shouldPresentStepUp.toggle()
                    } else {
                        // Refresh access token
                    }
                }
            }
            await getBankAccountDetails()
        }
    }
    
    func getBankAccountDetails() async -> AccountsDetailsResponse {
        var accountDetails: AccountsDetailsResponse? = nil
        let username = await CredentialManager(creds: nil).getUserInfo()
        
        do {
            let bankAPI = BankAPIManager()
            accountDetails = try await bankAPI.fetchAccountsDetails()
            
            self.username = username!
            if(!(accountDetails?.accounts.isEmpty)!) {
                self.balance = accountDetails!.accounts[0].balance!
                print("Account balance is: \(self.balance)")
            } else {
                print("getBankAccountDetails(): accountDetails.accounts is empty")
            }
        } catch {
            
        }
        return accountDetails!
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
