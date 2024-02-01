//
//  PKBankView.swift
//  PKBank
//
//  Created by Dennis Hills on 12/13/23.
//
import SwiftUI

struct PKBankAccountView: View {
    @Binding var isAuthenticated: Bool
    @Binding var isSteppingUp: Bool
    @State private var shouldPresentStepUp = false
    @State private var username: String = ""
    @State private var balance: Double = 0.00
    
    var body: some View {
        Button(""){
        }
        .onChange(of: isSteppingUp) { newValue in
            print("isSteppingUp changed to \(isSteppingUp)")
            if isAuthenticated {
                Task {
                    let accountDetails = await getBankAccountDetails()
                    if(accountDetails.accounts.isEmpty){
                        await getBankAccountDetails()
                    } else {
                        // Check for any pending transactions
                        print("BankAccountView: onAppear - checking for pending transactions")
                        let queuedTransactions = TransactionQueueManager.shared.retrieveTransactions()
                        if(!queuedTransactions.isEmpty){
                            print("BankAccountView: onAppear - Found 1 pending transactions")
                            let pendingTransaction = queuedTransactions[0]
                            Task {
                                let currentTransaction = Transaction(transactionType: pendingTransaction.transactionType, amount: pendingTransaction.amount, desc: pendingTransaction.desc)
                                await bankTransaction(currentTransaction)
                            }
                        } else {
                            print("BankAccountView: onAppear - No pending transactions")
                        }
                    }
                }
            }
        }
        .confirmationDialog("STEP UP REQUIRED", isPresented: $shouldPresentStepUp) {
            Button("UNLOCK WITH SECURITY KEY") {
                // Call step-up authentication
                PKBankLoginView(isAuthenticated: $isAuthenticated, isSteppingUp: $isSteppingUp).authenticate(action_type: PKBankLoginView.ActionType.STEPUP, username)
            }
        }
        if !isAuthenticated {
            PKBankLoginView(isAuthenticated: $isAuthenticated, isSteppingUp: $isSteppingUp)
        }
        Text("PK Bank Account")
            .onAppear(perform: {
                Task {
                    print("BankAccountView: entered onAppear")
                    if isAuthenticated {
                        let accountDetails = await getBankAccountDetails()
                        if(accountDetails.accounts.isEmpty){
                            await getBankAccountDetails()
                        } else {
                            // Check for any pending transactions
                            print("BankAccountView: onAppear - checking for pending transactions")
                            let queuedTransactions = TransactionQueueManager.shared.retrieveTransactions()
                            if(!queuedTransactions.isEmpty){
                                print("BankAccountView: onAppear - Found 1 pending transactions")
                                let pendingTransaction = queuedTransactions[0]
                                Task {
                                    let currentTransaction = Transaction(transactionType: pendingTransaction.transactionType, amount: pendingTransaction.amount, desc: pendingTransaction.desc)
                                    await bankTransaction(currentTransaction)
                                }
                            } else {
                                print("BankAccountView: onAppear - No pending transactions")
                            }
                        }
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
            } catch  {
                if(error as! BankAPIError == BankAPIError.requestFailed) {
                    let trans = Transaction(transactionType: transaction.transactionType, amount: transaction.amount, desc: transaction.desc)
                    TransactionQueueManager.shared.addTransaction(trans)
                    if(transaction.amount >= 1000.00){
                        shouldPresentStepUp.toggle()
                    } else {
                        // Refresh access token
                        let refreshed = await CredentialManager(creds: nil).renewAccessTokenWithRefreshToken()
                        // If refreshed token successful, retry pending transaction
                        if(refreshed){
                            await bankTransaction(trans)
                        }
                    } //end else
                } // end if error
            } // end catch #1
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
