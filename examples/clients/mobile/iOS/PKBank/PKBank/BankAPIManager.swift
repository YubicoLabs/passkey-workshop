//
//  APIManager.swift
//  PKBank
//
//  Created by Dennis Hills on 12/1/23.
//
import Foundation
import SimpleKeychain

class BankAPIManager {
    
    // Get Account Details for Bank User - /v1/account/{accountId} (GET)
    func fetchAccountDetails(accountId: Int) async throws -> AccountDetailsResponse? {
        var accountDetailsResponse: AccountDetailsResponse? = nil
        let session = URLSession.shared
        var request = URLRequest(url: getURLEndpoint(endpoint: Endpoint.accountDetails, accountId)!)
        
        let accessToken = CredentialManager(creds: nil).getAccessTokenLocal()
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        request.setValue("Bearer \(String(describing: accessToken))", forHTTPHeaderField: "Authorization")

        do {
            let (data, response) = try await session.data(for: request)
            
            guard let httpResponse = response as? HTTPURLResponse, (200 ..< 299) ~= httpResponse.statusCode else {
                data.printPrettyJSON("fetchAccountDetails invalidResponse")
                throw BankAPIError.invalidResponse
            }
            do {
                data.printPrettyJSON("Received AccountDetails from Bank API")
                accountDetailsResponse = try JSONDecoder().decode(AccountDetailsResponse.self, from: data)
            } catch {
                print("AccountDetailsResponse decoding error:", error)
                throw BankAPIError.parsingFailed
            }
        } catch {
            throw BankAPIError.requestFailed
        }
        return accountDetailsResponse
    }
    
    // Get Account Details for Bank User - /v1/accounts/ (GET)
    // Param: Bearer access token
    func fetchAccountsDetails() async throws -> AccountsDetailsResponse {
        var accountsDetailsResponse: AccountsDetailsResponse? = nil
        let session = URLSession.shared
        var request = URLRequest(url: getURLEndpoint(endpoint: Endpoint.accounts, -1)!)
        
        let accessToken = CredentialManager(creds: nil).getAccessTokenLocal()
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        request.setValue("Bearer \(String(describing: accessToken!))", forHTTPHeaderField: "Authorization")
        
        do {
            let (data, response) = try await session.data(for: request)
            
            guard let httpResponse = response as? HTTPURLResponse, (200 ..< 299) ~= httpResponse.statusCode else {
                data.printPrettyJSON("fetchAccountsDetails invalidResponse")
                throw BankAPIError.invalidResponse
            }
            do {
                data.printPrettyJSON("Received AccountDetails from Bank API")
                accountsDetailsResponse = try JSONDecoder().decode(AccountsDetailsResponse.self, from: data)
                
                saveAccountDetailsLocal(accountsDetailsResponse?.accounts[0])
                
                return accountsDetailsResponse!
            } catch {
                print("AccountsDetailsResponse decoding error:", error)
                throw BankAPIError.parsingFailed
            }
        } catch {
            throw BankAPIError.requestFailed
        }
        //return accountsDetailsResponse!
    }
    
    func saveAccountDetailsLocal(_ accountDetails: AccountDetailsResponse?) -> Bool {
        let keychain = SimpleKeychain(service: "PKBank")
        guard let accountDetails else { return false }
        
        do {
            try keychain.set(String(accountDetails.accountId), forKey: "accountId")
            try keychain.set(String(accountDetails.balance!), forKey: "balance")
            return true
        } catch {
            print("Error saving account details to iOS Keychain: \(error)")
            return false
        }
    }
    
    func getAccountDetailsLocal() -> AccountDetailsResponse {
        var accountDetails: AccountDetailsResponse? = AccountDetailsResponse(accountId: 0, balance: 0.00)
        var accountId: Int
        var balance: Double
        
        let keychain = SimpleKeychain(service: "PKBank")
        
        do {
            let accountId = try keychain.string(forKey: "accountId")
            let balance = try keychain.string(forKey: "balance")
            accountDetails?.accountId = Int(accountId)!
            accountDetails?.balance = Double(balance)
    
            return accountDetails!
        } catch {
            print("Error retrieving access token from iOS Keychain: \(error)")
        }
        return accountDetails!
    }
    
    func makeBankTransaction(transactionType: BankTransactionType, amount: Double, desc: String) async throws -> BankTransactionResponse {
        var transactionResponse: BankTransactionResponse? = nil
        let session = URLSession.shared
        var request = URLRequest(url: getURLEndpoint(endpoint: Endpoint.transactions, getAccountDetailsLocal().accountId)!)
        
        let accessToken = CredentialManager(creds: nil).getAccessTokenLocal()
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        request.setValue("Bearer \(String(describing: accessToken!))", forHTTPHeaderField: "Authorization")
        
        let transactionRequest = BankTransactionRequest(type: transactionType.rawValue, amount: amount, description: desc)
        // Convert model to JSON to send as body
        guard let jsonBodyData = try? JSONEncoder().encode(transactionRequest) else { return transactionResponse! }
        jsonBodyData.printPrettyJSON("Sending Bank Transaction request to Bank API")
        request.httpBody = jsonBodyData
        
        do {
            let (data, response) = try await session.data(for: request)
            
            guard let httpResponse = response as? HTTPURLResponse, (200 ..< 299) ~= httpResponse.statusCode else {
                data.printPrettyJSON("makeBankTransaction invalidResponse")
                throw BankAPIError.invalidResponse
            }
            do {
                data.printPrettyJSON("Received BankTransactionResponse from Bank API")
                transactionResponse = try JSONDecoder().decode(BankTransactionResponse.self, from: data)
                return transactionResponse!
            } catch {
                print("BankTransactionResponse decoding error:", error)
                throw BankAPIError.parsingFailed
            }
        } catch {
            throw BankAPIError.requestFailed
        }
    }
    
    func getBankTransactions() async throws -> BankTransactions {
        var transactionsResponse: BankTransactions? = nil
        let session = URLSession.shared
        var request = URLRequest(url: getURLEndpoint(endpoint: Endpoint.transactions, getAccountDetailsLocal().accountId)!)
        
        let accessToken = CredentialManager(creds: nil).getAccessTokenLocal()
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        request.setValue("Bearer \(String(describing: accessToken!))", forHTTPHeaderField: "Authorization")
        
        do {
            let (data, response) = try await session.data(for: request)
            
            guard let httpResponse = response as? HTTPURLResponse, (200 ..< 299) ~= httpResponse.statusCode else {
                data.printPrettyJSON("getBankTransactions invalidResponse")
                throw BankAPIError.invalidResponse
            }
            do {
                data.printPrettyJSON("Received BankTransactionsResponse from Bank API")
                transactionsResponse = try JSONDecoder().decode(BankTransactions.self, from: data)
                return transactionsResponse!
            } catch {
                print("BankTransactions decoding error:", error)
                throw BankAPIError.parsingFailed
            }
        } catch {
            throw BankAPIError.requestFailed
        }
    }
    
    func getURLEndpoint(endpoint: Endpoint, _ accountId: Int) -> URL? {
        switch endpoint {
            case .status :
                return URL(string: BANKAPI.domain + "/v1/status")
            case .accounts :
                return URL(string: BANKAPI.domain + "/v1/accounts")
            case .transactions:
                return URL(string: BANKAPI.domain + "/v1/account/\(accountId)/transactions")
            case .accountDetails:
                return URL(string: BANKAPI.domain + "/v1/account/\(String(describing: accountId))")
        }
    }
}

enum BankAPIError: Error {
    case invalidURL
    case requestFailed
    case invalidResponse
    case parsingFailed
}

enum Endpoint {
    case accounts
    case accountDetails
    case status
    case transactions
}

enum BankTransactionType : String {
    case deposit
    case withdraw
    case transfer
}

struct BankTransactionRequest: Encodable {
    let type: String
    let amount: Double
    let description: String
}

struct BankTransactions: Decodable {
    let transactions: [BankTransactionResponse]
}

struct BankTransactionResponse: Decodable {
    let transactionId: Int?
    let type: String?
    let amount: Double?
    let transactionDate: String?
    let description: String?
}

// For account details /v1/account/{accountId}
struct AccountDetailsRequest: Encodable {
    let accountId: Int

    private enum CodingKeys: String, CodingKey {
        case accountId
    }
}

// Account Details server response from /v1/account/{accountId}
struct AccountDetailsResponse: Decodable {
    var accountId: Int
    var balance: Double?
}

struct AccountsDetailsResponse: Decodable {
    let accounts: [AccountDetailsResponse]
}

struct BankAPIStatus: Decodable {
    let status: String
}
