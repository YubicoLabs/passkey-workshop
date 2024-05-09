//
//  RelyingParty.swift
//  PawsKey
//
//  Created by Dennis Hills on 3/13/23.
//  Copyright © 2023 Yubico. All rights reserved.
//
//  This class handles the network request/response to/from the Relying Party server API
//
import Foundation

class RelyingParty {
    
    static let API_ENDPOINT = "https://replace-with-your-hostname.trycloudflare.com/v1"
    
    // #ATTESTATION OPTIONS
    
    // Get Attestation Options for user - /v1/attestation/options (POST)
    func fetchAttestationOptions(optionsRequest: AttestationOptionsRequest) async throws -> AttestationOptionsResponse? {
        var attestationOptionsResponse: AttestationOptionsResponse? = nil
        let session = URLSession.shared

        // Encode optionsRequest model to JSON data to send as our body payload
        guard let jsonBodyData = try? JSONEncoder().encode(optionsRequest) else { return nil }

        jsonBodyData.printPrettyJSON("Sending AttestationOptions request to RP")

        var request = URLRequest(url: getURLEndpoint(endpoint: Endpoint.attestationOptions)!)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        request.httpBody = jsonBodyData

        do {
            let (data, response) = try await session.data(for: request)
            
            guard let httpResponse = response as? HTTPURLResponse, (200 ..< 299) ~= httpResponse.statusCode else {
                data.printPrettyJSON("fetchAttestationOptions invalidResponse")
                throw RelyingPartyError.invalidResponse
            }
            
            do {
                data.printPrettyJSON("Received AttestationOptions from RP")
                attestationOptionsResponse = try JSONDecoder().decode(AttestationOptionsResponse.self, from: data)
            } catch {
                throw RelyingPartyError.parsingFailed
            }

        } catch {
            throw RelyingPartyError.requestFailed
        }
        return attestationOptionsResponse
    }
    
    // #ATTESTATION RESULTS
    // Get Attestation Options - /v1/attestation/result (POST)
    func sendAttestationResults(attestationResults: AttestationResultsRequest) async throws -> AttestationResultsStatus? {
        var attestationResultStatus: AttestationResultsStatus?
        let session = URLSession.shared
        // Convert model to JSON to send as body
        guard let jsonBodyData = try? JSONEncoder().encode(attestationResults) else { return nil }
        
        jsonBodyData.printPrettyJSON("Sending AttestationResult to RP")
        
        var request = URLRequest(url: getURLEndpoint(endpoint: Endpoint.attestationResult)!)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        request.httpBody = jsonBodyData
        
        do {
            let (data, response) = try await session.data(for: request)
            
            guard let httpResponse = response as? HTTPURLResponse, (200 ..< 299) ~= httpResponse.statusCode else {
                data.printPrettyJSON("sendAttestationResults invalidResponse")
                throw RelyingPartyError.invalidResponse
            }
            
            do {
                data.printPrettyJSON("Received AttestationResult from RP")
                attestationResultStatus = try JSONDecoder().decode(AttestationResultsStatus.self, from: data)
            } catch {
                throw RelyingPartyError.parsingFailed
            }
        } catch {
            throw RelyingPartyError.requestFailed
        }
        return attestationResultStatus
    }
    
    func fetchAssertionOptions(optionsRequest: AssertionOptionsRequest) async throws -> AssertionOptionsResponse? {
        var assertionOptionsResponse: AssertionOptionsResponse? = nil
        let session = URLSession.shared
        
        // Convert model to JSON to send as body
        guard let jsonBodyData = try? JSONEncoder().encode(optionsRequest) else {
            return nil
        }
        
        jsonBodyData.printPrettyJSON("Sending AssertionOptions request to RP")
        
        var request = URLRequest(url: getURLEndpoint(endpoint: Endpoint.assertionOptions)!)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        request.httpBody = jsonBodyData
    
        do {
            let (data, response) = try await session.data(for: request)
            
            guard let httpResponse = response as? HTTPURLResponse, (200 ..< 299) ~= httpResponse.statusCode else {
                data.printPrettyJSON("fetchAssertionOptions invalidResponse")
                throw RelyingPartyError.invalidResponse
            }
            
            do {
                data.printPrettyJSON("Received AssertionOptions response from RP")
                assertionOptionsResponse = try JSONDecoder().decode(AssertionOptionsResponse.self, from: data)
            } catch {
                throw RelyingPartyError.parsingFailed
            }
        } catch {
            throw RelyingPartyError.requestFailed
        }
        return assertionOptionsResponse
    }
    
    // #ASSERTION RESULTS
    // Send public-key assertion response to relying party
    func sendAssertionResults(assertionResults: AssertionResults) async throws -> AssertionResultsStatus? {
        var assertionResultsStatus: AssertionResultsStatus? = nil
        let session = URLSession.shared
        
        // Convert model to JSON to send as body
        guard let jsonBodyData = try? JSONEncoder().encode(assertionResults) else {
            return nil
        }
        jsonBodyData.printPrettyJSON("Sending AssertionResults to RP")
        
        var request = URLRequest(url: getURLEndpoint(endpoint: Endpoint.assertionResult)!)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        request.httpBody = jsonBodyData
        
        do {
            let (data, response) = try await session.data(for: request)
            
            guard let httpResponse = response as? HTTPURLResponse, (200 ..< 299) ~= httpResponse.statusCode else {
                data.printPrettyJSON("sendAssertionResults invalidResponse")
                throw RelyingPartyError.invalidResponse
            }
            do {
                data.printPrettyJSON("Received AssertionResult Status from RP:")
                assertionResultsStatus = try JSONDecoder().decode(AssertionResultsStatus.self, from: data)
            } catch {
                throw RelyingPartyError.parsingFailed
            }
        } catch {
            throw RelyingPartyError.requestFailed
        }
        return assertionResultsStatus
    }
    
    func getURLEndpoint(endpoint: Endpoint) -> URL? {
        switch endpoint {
            case .attestationOptions :
                return URL(string: RelyingParty.API_ENDPOINT + "/attestation/options")
            case .attestationResult:
                return URL(string: RelyingParty.API_ENDPOINT + "/attestation/result")
            case .assertionOptions:
                return URL(string: RelyingParty.API_ENDPOINT + "/assertion/options")
            case .assertionResult:
                return URL(string: RelyingParty.API_ENDPOINT + "/assertion/result")
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
            print("Error printing pretty JSON:\(error.localizedDescription)")
        }
    }
    
    private func printJSONData(_ str: String, _ data: Data) {
        print(str + ":\n" + String(decoding: data, as: UTF8.self) + "\n")
    }
}

enum Endpoint {
    case attestationOptions
    case attestationResult
    case assertionOptions
    case assertionResult
}

enum RelyingPartyError: Error {
    case invalidURL
    case requestFailed
    case invalidResponse
    case parsingFailed
}

// Response to a new credential creation
struct AttestationResultsStatus: Decodable {
    let status: String?
}

// For sending /v1/attestation/options
struct AttestationOptionsRequest: Encodable {
    let userName: String
    var displayName = "Pawskey iOS User"
    let authenticatorSelection = AuthenticatorSelection()
    var attestation = "direct"
    
    private enum CodingKeys: String, CodingKey {
        case userName, displayName, authenticatorSelection
    }
}

// For sending to /v1/attestation/result after authenticator generates creds
struct AttestationResultsRequest: Encodable {
    let requestId: String
    let makeCredentialResult: MakeCredentialResult
}
                                                                               
struct MakeCredentialResult: Encodable {
   let id: String
   let response: AuthenticatorResponse
   let type = "public-key"
   let clientExtensionResults = ClientExtensionResults() // Working as an empty {}
}
                                                                               
struct AuthenticatorResponse: Encodable {
   let clientDataJSON: String
   let attestationObject: String
}
                                                                               
// Server response from /v1/attestation/options
struct AttestationOptionsResponse: Decodable {
    let requestId: String
    let publicKey: PublicKey
    
    struct PublicKey: Decodable {
        let rp: RelyingParty
        let user: User
        let challenge: String
        let pubKeyCredParams: [PublicKeyCredParams]
        let excludeCredentials: [Credentials]?
        let authenticatorSelection: AuthenticatorSelection?
        let attestation: String?
    }
    
    struct RelyingParty: Decodable {
        let name: String
        let id: String
    }
    
    struct User: Decodable {
        let id: String
        let name: String
        var displayName: String?
    }
    
    struct PublicKeyCredParams: Decodable {
        let type: String
        let alg: Int
    }
    
    struct Credentials: Decodable {
        let type: String
        let id: String
    }
}

struct AuthenticatorSelection: Encodable, Decodable {
    var residentKey: String?
    var authenticatorAttachment: String?
    var userVerification: String?
    
    private enum CodingKeys: String, CodingKey {
        case residentKey, userVerification
    }
}

struct ClientExtensionResults: Encodable, Decodable {}

// Assertions
struct AssertionOptionsRequest: Encodable {
    let userName: String?
}

// Endpoint: /v1/assertion/options
struct AssertionOptionsResponse: Decodable {
    let requestId: String
    let publicKey: PublicKey
    let errorMessage: String?
    
    struct PublicKey: Decodable {
        let challenge: String
        let timeout: Int?
        let rpId: String
        let allowCredentials: [Credentials]?
        let userVerification: String?
    }
    
    struct Credentials: Decodable {
        let id: String
        var type = "public-key"
    }
}

// Endpoint: /v1/assertion/result
// Use this model to encode as JSON body back to finalize assertion
struct AssertionResults: Encodable {
    let requestId: String
    let assertionResult: AssertionResult
    
    struct AssertionResult: Encodable {
        let id: String
        let response: Response
        var type = "public-key"
        let clientExtensionResults = ClientExtensionResults()
    }
    
    struct Response: Encodable {
        let authenticatorData: String
        let signature: String
        let userHandle: String
        let clientDataJSON: String
    }
}

struct AssertionResultsStatus: Decodable {
    let status: String
    var error: String?
}

struct APIStatus: Decodable {
    let status: String
}
