//
//  APIManager.swift
//  PKBank
//
//  Created by Dennis Hills on 12/1/23.
//
import Foundation

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
                print("decoding error:", error)
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
        var request = URLRequest(url: getURLEndpoint(endpoint: Endpoint.accounts, nil)!)
        
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
                return accountsDetailsResponse!
            } catch {
                print("decoding error:", error)
                throw BankAPIError.parsingFailed
            }
        } catch {
            throw BankAPIError.requestFailed
        }
        //return accountsDetailsResponse!
    }
    
//    func fetchAccountsDetails() async throws -> AccountsDetailsResponse {
//        var request = URLRequest(url: getURLEndpoint(endpoint: Endpoint.accounts, nil)!)
//        
//        let accessToken = CredentialManager(creds: nil).getAccessTokenLocal()
//        request.setValue("application/json", forHTTPHeaderField: "Accept")
//        request.setValue("Bearer \(String(describing: accessToken!))", forHTTPHeaderField: "Authorization")
//        
//        do {
//            let (data, _) = try await URLSession.shared.data(for: request)
//            let decoder = JSONDecoder()
//            let response = try decoder.decode(AccountsDetailsResponse.self, from: data)
//            return response
//        } catch {
//            throw BankAPIError.requestFailed
//        }
//    }
    
    func getURLEndpoint(endpoint: Endpoint, _ accountId: Int?) -> URL? {
        switch endpoint {
            case .status :
                return URL(string: BANKAPI.domain + "/v1/status")
            case .accounts :
                return URL(string: BANKAPI.domain + "/v1/accounts")
            case .transactions:
                return URL(string: BANKAPI.domain + "/v1/account/\(String(describing: accountId))/transactions")
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

// For account details /v1/account/{accountId}
struct AccountDetailsRequest: Encodable {
    let accountId: Int

    private enum CodingKeys: String, CodingKey {
        case accountId
    }
}

// Account Details server response from /v1/account/{accountId}
struct AccountDetailsResponse: Decodable {
    let accountId: Int
    let balance: Double?
}

struct AccountsDetailsResponse: Decodable {
    let accounts: [AccountDetailsResponse]
}

struct BankAPIStatus: Decodable {
    let status: String
}

