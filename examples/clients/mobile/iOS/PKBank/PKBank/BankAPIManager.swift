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
        var request = URLRequest(url: getURLEndpoint(endpoint: Endpoint.accountDetails, param: accountId)!)
        
        let accessToken = CredentialManager(creds: nil).getAccessToken()
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        request.setValue("Bearer \(String(describing: accessToken))", forHTTPHeaderField: "Authorization")

        do {
            let (data, response) = try await session.data(for: request)
            
            guard let httpResponse = response as? HTTPURLResponse, (200 ..< 299) ~= httpResponse.statusCode else {
                data.printPrettyJSON("fetchAccountDetails invalidResponse")
                throw APIError.invalidResponse
            }
            do {
                data.printPrettyJSON("Received AccountDetails from Bank API")
                accountDetailsResponse = try JSONDecoder().decode(AccountDetailsResponse.self, from: data)
            } catch {
                throw APIError.parsingFailed
            }

        } catch {
            throw APIError.requestFailed
        }
        return accountDetailsResponse
    }
    
    func getURLEndpoint(endpoint: Endpoint, param: Int?) -> URL? {
        switch endpoint {
            case .status :
                return URL(string: BankAPI.baseURI + "/status")
            case .accounts :
                return URL(string: BankAPI.baseURI + "/accounts")
            case .transactions:
                return URL(string: BankAPI.baseURI + "/account/{accountId}/transactions")
            case .accountDetails:
            return URL(string: BankAPI.baseURI + "/account/\(String(describing: param))")
        }
    }
}

enum APIError: Error {
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
    let accountId: String
    let balance: Double
}

struct BankAPIStatus: Decodable {
    let status: String
}

