/*
 
*/

import AuthenticationServices
import Foundation
import os

extension NSNotification.Name {
    static let UserSignedIn = Notification.Name("UserSignedInNotification")
    static let UserFailedSignIn = Notification.Name("UserFailedSignInNotification")
    static let ModalSignInSheetCanceled = Notification.Name("ModalSignInSheetCanceledNotification")
}

class AccountManager: NSObject, ASAuthorizationControllerPresentationContextProviding, ASAuthorizationControllerDelegate {
    
    var authenticationAnchor: ASPresentationAnchor?
    var isPerformingModalReqest = false
    var attestationOptionsRequest: AttestationOptionsRequest?
    var attestationOptionsResponse: AttestationOptionsResponse?
    var attestationResultsRequest: AttestationResultsRequest?
    var attestationResultsStatus: AttestationResultsStatus?
    var assertionOptionsResponse: AssertionOptionsResponse?
    var assertionResults: AssertionResults?
    
    var requestId: String?
    var userName: String?
    
    // [ATTESTATION] [Security Key] - Registration of a new Security Key
    func signUpWithSecurityKey(userName: String, anchor: ASPresentationAnchor) {
        self.authenticationAnchor = anchor
        
        if(userName.isEmpty){ return }
        self.userName = userName
        
        // Initialize SecurityKey ASAuthorization provider
        let securityKeyCredentialProvider = ASAuthorizationSecurityKeyPublicKeyCredentialProvider(relyingPartyIdentifier: RPID.domain)

        let rp = RelyingParty()

        // Fetch the registration options from the server and then make a security key registration request using
        // Apple's AuthenticationServices Framework
        Task {
            
            do {
                try await self.attestationOptionsResponse = rp.fetchAttestationOptions(optionsRequest: AttestationOptionsRequest(userName: self.userName!))
            } catch {
                throw error
            }
            
            // Capture relying party attestation options
            self.requestId = self.attestationOptionsResponse?.requestId
            
            let challenge = Data(base64urlEncoded: (self.attestationOptionsResponse?.publicKey.challenge)!)
            let userID = Data(base64urlEncoded: (self.attestationOptionsResponse?.publicKey.user.id)!)
            let displayName = self.attestationOptionsResponse?.publicKey.user.displayName
            let publicKeyAlgorithms = getPublicKeyAlgorithms((self.attestationOptionsResponse?.publicKey.pubKeyCredParams)!)
           
            // Initialize an ASAuthorizationSecurityKeyPublicKeyCredentialRegistrationRequest
            let securityKeyRegistrationRequest =
                securityKeyCredentialProvider.createCredentialRegistrationRequest(challenge: challenge!, displayName: displayName!, name: userName, userID: userID!)
            
            // Set request options to the Security Key provider
            securityKeyRegistrationRequest.credentialParameters = publicKeyAlgorithms
            
            if let residentCredPref = self.attestationOptionsResponse?.publicKey.authenticatorSelection?.residentKey {
                securityKeyRegistrationRequest.residentKeyPreference = residentKeyPreference(residentCredPref)
            }
            
            if let userVerificationPref = self.attestationOptionsResponse?.publicKey.authenticatorSelection?.userVerification {
                securityKeyRegistrationRequest.userVerificationPreference = userVerificationPreference(userVerificationPref)
            }
            
            if let rpAttestationPref = self.attestationOptionsResponse?.publicKey.attestation {
                securityKeyRegistrationRequest.attestationPreference = attestationStatementPreference(rpAttestationPref)
            }
            
            if let excludedCredentials = self.attestationOptionsResponse?.publicKey.excludeCredentials {
                if(!excludedCredentials.isEmpty){
                    securityKeyRegistrationRequest.excludedCredentials = credentialAttestationDescriptor(credentials: excludedCredentials)!
                }
            }
        
            let authController = ASAuthorizationController(authorizationRequests: [ securityKeyRegistrationRequest ] )
            authController.delegate = self
            authController.presentationContextProvider = self
            authController.performRequests()
            isPerformingModalReqest = true
        }
    }
    
    // [ATTESTATION] [Platform] - Registration of a new platform passkey
    func signUpWithPlatform(userName: String, anchor: ASPresentationAnchor) {
        self.authenticationAnchor = anchor
        
        if(userName.isEmpty){ return }
        self.userName = userName
        
        let publicKeyPlatformCredentialProvider = ASAuthorizationPlatformPublicKeyCredentialProvider(relyingPartyIdentifier: RPID.domain)

        let rp = RelyingParty()

        // Fetch the registration options from the server and then make a passkey registration request
        Task {
            do {
                try await self.attestationOptionsResponse = rp.fetchAttestationOptions(optionsRequest: AttestationOptionsRequest(userName: self.userName!))
            } catch {
                throw error
            }
            
            // Capture relying party requestId
            self.requestId = self.attestationOptionsResponse?.requestId
            
            let challenge = Data(base64urlEncoded: (self.attestationOptionsResponse?.publicKey.challenge)!) // Base64URL Encoded
            let userID = Data(base64urlEncoded: (self.attestationOptionsResponse?.publicKey.user.id)!) // Base64URL Encoded
            
            let platformRegistrationRequest = publicKeyPlatformCredentialProvider.createCredentialRegistrationRequest(
                challenge: challenge!, name: userName, userID: userID!)

            // Use only ASAuthorizationPlatformPublicKeyCredentialRegistrationRequests or
            // ASAuthorizationSecurityKeyPublicKeyCredentialRegistrationRequests here. NOT BOTH?
            let authController = ASAuthorizationController(authorizationRequests: [ platformRegistrationRequest ] )
            authController.delegate = self
            authController.presentationContextProvider = self
            authController.performRequests()
            isPerformingModalReqest = true
        }
    }
    // [ASSERTION] [Security Key] - Security Key Authentication
    func signInWithSecurityKey(userName: String, anchor: ASPresentationAnchor) {
        self.authenticationAnchor = anchor
        self.userName = userName
        
        let publicKeySecurityKeyCredentialProvider = ASAuthorizationSecurityKeyPublicKeyCredentialProvider(relyingPartyIdentifier: RPID.domain)
        
        // Fetch the assertion options from the server and then make a passkey assertion request
        let rp = RelyingParty()
        
        Task {
            do {
                try await self.assertionOptionsResponse = rp.fetchAssertionOptions(optionsRequest: AssertionOptionsRequest(userName: self.userName))
            } catch {
                throw error
            }
            
            // Capture relying party requestId
            self.requestId = self.assertionOptionsResponse?.requestId
            let challenge = Data(base64urlEncoded: (self.assertionOptionsResponse?.publicKey.challenge)!)
            let allowCredentials = self.assertionOptionsResponse?.publicKey.allowCredentials
            
            let securityKeyAssertionRequest = publicKeySecurityKeyCredentialProvider.createCredentialAssertionRequest(challenge: challenge!)
            
            // If excludeCredentials were returned by the RP, pass these into the request as an [ASAuthorizationSecurityKeyPublicKeyCredentialDescriptor]
            if(!allowCredentials!.isEmpty){
                securityKeyAssertionRequest.allowedCredentials = credentialAssertionDescriptor(credentials: allowCredentials!)!
            }
            
            // Security Key ONLY
            let authController = ASAuthorizationController(authorizationRequests: [ securityKeyAssertionRequest ] )
            
            authController.delegate = self
            authController.presentationContextProvider = self
            authController.performRequests()
            isPerformingModalReqest = true
        }
    }
    
    // [ASSERTION] [Platform] Platform authentication
    func signInWithPlatform(userName: String, anchor: ASPresentationAnchor) {
        self.authenticationAnchor = anchor
        self.userName = userName

        let publicKeyPlatformCredentialProvider = ASAuthorizationPlatformPublicKeyCredentialProvider(relyingPartyIdentifier: RPID.domain)

        // Fetch the assertion options from the server and then make a passkey assertion request
        let rp = RelyingParty()

        Task {
            do {
                try await self.assertionOptionsResponse = rp.fetchAssertionOptions(optionsRequest: AssertionOptionsRequest(userName: self.userName!))
            } catch {
                throw error
            }

            // Capture relying party requestId
            self.requestId = self.assertionOptionsResponse?.requestId
            let challenge = Data(base64urlEncoded: (self.assertionOptionsResponse?.publicKey.challenge)!)

            let platformAssertionRequest = publicKeyPlatformCredentialProvider.createCredentialAssertionRequest(challenge: challenge!)

            let authController = ASAuthorizationController(authorizationRequests: [ platformAssertionRequest ] )
            authController.delegate = self
            authController.presentationContextProvider = self
            authController.performRequests()
            isPerformingModalReqest = true
        }
    }
    
    // NOT IMPLEMENTED YET: This function will handle SignIn [ASSERTION] for both [Platform] & [Security Key] as passkeys
    func signInWithPasskey(userName: String, anchor: ASPresentationAnchor) {
        self.authenticationAnchor = anchor
        self.userName = userName

        let publicKeyPlatformCredentialProvider = ASAuthorizationPlatformPublicKeyCredentialProvider(relyingPartyIdentifier: RPID.domain)
        let publicKeySecurityKeyCredentialProvider = ASAuthorizationSecurityKeyPublicKeyCredentialProvider(relyingPartyIdentifier: RPID.domain)
        
        // Fetch the assertion options from the server and then make a passkey assertion request
        let rp = RelyingParty()

        Task {
            do {
                try await self.assertionOptionsResponse = rp.fetchAssertionOptions(optionsRequest: AssertionOptionsRequest(userName: self.userName))
            } catch {
                throw error
            }

            // Capture relying party requestId
            self.requestId = self.assertionOptionsResponse?.requestId
            let challenge = Data(base64urlEncoded: (self.assertionOptionsResponse?.publicKey.challenge)!)

            let platformAssertionRequest = publicKeyPlatformCredentialProvider.createCredentialAssertionRequest(challenge: challenge!)
            let securityKeyAssertionRequest = publicKeySecurityKeyCredentialProvider.createCredentialAssertionRequest(challenge: challenge!)
            
            let authController = ASAuthorizationController(authorizationRequests: [ platformAssertionRequest, securityKeyAssertionRequest ] )
            authController.delegate = self
            authController.presentationContextProvider = self
            authController.performRequests()
            isPerformingModalReqest = true
        }
    }
    
    // MARK: - ASAuthorizationController Callback
    
    // ASAuthorization delegate callback for ALL passkey registration and authentication events
    // There are FOUR(4) passkey credential types captured here:
    // TWO(2) registration passkey credentials: Platform & SecurityKey
    // TWO(2) authentication(assertion) passkey credentials: Platform & SecurityKey
    // The funtion determines the Authorization provider type: Platform & SecurityKey
    // then determine how to introspect the credential(s)
    func authorizationController(controller: ASAuthorizationController, didCompleteWithAuthorization authorization: ASAuthorization) {
        let logger = Logger()
        
        // ASAuthorizationCredential
        switch authorization.credential {
        
        // [REGISTRATION] [Platform]
        case let platformRegistration as ASAuthorizationPlatformPublicKeyCredentialRegistration:
            logger.log("NEW PLATFORM passkey registered\n[credentialId]: \(platformRegistration.credentialID.toBase64urlEncodedString())\n")
            
            // Prepare response objects in the format the RP expects: Base64URLString
            guard let attestationObject = platformRegistration.rawAttestationObject?.toBase64urlEncodedString() else {return}
            let clientDataJSON = platformRegistration.rawClientDataJSON.toBase64urlEncodedString()
            let credentialId = platformRegistration.credentialID.toBase64urlEncodedString()
            let authenticatorResponse = AuthenticatorResponse(clientDataJSON: clientDataJSON, attestationObject: attestationObject)
            let makeCredentialResult = MakeCredentialResult(id: credentialId, response: authenticatorResponse)
            
            // Send the makeCredentialResult to the Relying Party
            self.attestationResultsRequest = AttestationResultsRequest(requestId: self.requestId!, makeCredentialResult: makeCredentialResult)
            
            let rp = RelyingParty()
            
            Task {
                do {
                    let registrationAttestationResultsStatus = try await rp.sendAttestationResults(attestationResults: self.attestationResultsRequest!)
                    if(registrationAttestationResultsStatus?.status == "error") {
                        DispatchQueue.main.async {
                            self.didFailSignIn()
                        }
                    } else {
                        DispatchQueue.main.async {
                            self.didFinishSignIn()
                        }
                    }
                } catch {
                    DispatchQueue.main.async {
                        self.didFailSignIn()
                    }
                }
            }
            
        // [REGISTRATION] [Security Key]
        case let securityKeyRegistration as ASAuthorizationSecurityKeyPublicKeyCredentialRegistration:
            logger.log("A new [Security Key] passkey was registered: \(securityKeyRegistration)")

            // Prepare response objects in the format the RP expects: Base64URLString
            guard let attestationObject = securityKeyRegistration.rawAttestationObject?.toBase64urlEncodedString() else {return}
            let clientDataJSON = securityKeyRegistration.rawClientDataJSON.toBase64urlEncodedString()
            let credentialId = securityKeyRegistration.credentialID.toBase64urlEncodedString()
            let authenticatorResponse = AuthenticatorResponse(clientDataJSON: clientDataJSON,               attestationObject: attestationObject)
            let makeCredentialResult = MakeCredentialResult(id: credentialId, response: authenticatorResponse)
            
            // Send the makeCredentialResult to the Relying Party
            self.attestationResultsRequest = AttestationResultsRequest(requestId: self.requestId!, makeCredentialResult: makeCredentialResult)
            
            let rp = RelyingParty()
            
            Task {
                do {
                    let registrationAttestationResultsStatus = try await rp.sendAttestationResults(attestationResults: self.attestationResultsRequest!)
                    if(registrationAttestationResultsStatus?.status == "error") {
                        DispatchQueue.main.async {
                            self.didFailSignIn()
                        }
                    } else {
                        DispatchQueue.main.async {
                            self.didFinishSignIn()
                        }
                    }
                } catch {
                    DispatchQueue.main.async {
                        self.didFailSignIn()
                    }
                }
            }
            
        // [ASSERTION] [Platform]
        case let platformAssertionRequest as ASAuthorizationPlatformPublicKeyCredentialAssertion:
            logger.log("A PLATFORM passkey was used to sign in\n[CredentialId]: \(platformAssertionRequest.credentialID.toBase64urlEncodedString())\n")
            
            // Prepare response objects in the format the RP expects: Base64URLString
            let userID = platformAssertionRequest.userID.toBase64urlEncodedString()
            let authenticatorData = platformAssertionRequest.rawAuthenticatorData.toBase64urlEncodedString()
            let signature = platformAssertionRequest.signature.toBase64urlEncodedString()
            let clientDataJSON = platformAssertionRequest.rawClientDataJSON.toBase64urlEncodedString()
            let credentialId = platformAssertionRequest.credentialID.toBase64urlEncodedString()
            
            // Build authenticator response object
            let authenticatorResponse = AssertionResults.Response(authenticatorData: authenticatorData, signature: signature, userHandle: userID, clientDataJSON: clientDataJSON)
            
            // Build final assertion result object for sending to relying party
            let assertionResult = AssertionResults.AssertionResult(id: credentialId, response: authenticatorResponse)
            
            // Assign assertion results to the RP
            self.assertionResults = AssertionResults(requestId: self.requestId!, assertionResult: assertionResult)
            
            let rp = RelyingParty()
            
            Task {
                do {
                    let assertionResultStatus = try await rp.sendAssertionResults(assertionResults: self.assertionResults!)!
                    if(assertionResultStatus.status == "error") {
                        DispatchQueue.main.async {
                            self.didFailSignIn()
                        }
                    } else {
                        DispatchQueue.main.async {
                            self.didFinishSignIn()
                        }
                    }
                } catch {
                    DispatchQueue.main.async {
                        self.didFailSignIn()
                    }
                }
            }
            
        // [ASSERTION] [Security Key]
        case let securityKeyAssertionRequest as ASAuthorizationSecurityKeyPublicKeyCredentialAssertion:
            logger.log("A Security Key was used to sign in\n[CredentialId]: \(securityKeyAssertionRequest.credentialID.toBase64urlEncodedString())\n")
            
            // Prepare response objects in the format the RP expects: Base64URLString
            let userID = securityKeyAssertionRequest.userID.toBase64urlEncodedString()
            let authenticatorData = securityKeyAssertionRequest.rawAuthenticatorData.toBase64urlEncodedString()
            let signature = securityKeyAssertionRequest.signature.toBase64urlEncodedString()
            let clientDataJSON = securityKeyAssertionRequest.rawClientDataJSON.toBase64urlEncodedString()
            let credentialId = securityKeyAssertionRequest.credentialID.toBase64urlEncodedString()
            
            // Build authenticator response object
            let authenticatorResponse = AssertionResults.Response(authenticatorData: authenticatorData, signature: signature, userHandle: userID, clientDataJSON: clientDataJSON)
            
            // Build final assertion result object for sending to relying party
            let assertionResult = AssertionResults.AssertionResult(id: credentialId, response: authenticatorResponse)
            
            // Assign assertion results to the RP
            self.assertionResults = AssertionResults(requestId: self.requestId!, assertionResult: assertionResult)
            
            let rp = RelyingParty()
            
            Task {
                do {
                    let assertionResultStatus = try await rp.sendAssertionResults(assertionResults: self.assertionResults!)!
                    if(assertionResultStatus.status == "error") {
                        DispatchQueue.main.async {
                            self.didFailSignIn()
                        }
                    } else {
                        DispatchQueue.main.async {
                            self.didFinishSignIn()
                        }
                    }
                } catch {
                    DispatchQueue.main.async {
                        self.didFailSignIn()
                    }
                }
            }
      
        default:
            fatalError("Received unknown authorization type.")
        }
        isPerformingModalReqest = false
    }

    // ASAuthorization delegate callback for capturing any ERROR during passkey attestation or assertion
    func authorizationController(controller: ASAuthorizationController, didCompleteWithError error: Error) {
        let logger = Logger()
        guard let authorizationError = error as? ASAuthorizationError else {
            isPerformingModalReqest = false
            logger.error("Unexpected authorization error: \(error.localizedDescription)")
            return
        }

        if authorizationError.code == .canceled {
            // Either the system doesn't find any credentials and the request ends silently, or the user cancels the request.
            logger.log("Request canceled.")

            if isPerformingModalReqest {
                didCancelModalSheet()
            }
        } else {
            // Another ASAuthorization error.
            // Note: The userInfo dictionary contains useful information.
            // 1000 = unknown, 1001 = canceled, 1002 = invalidResponse, 1003 = notHandled, 1004 = failed
            logger.error("ASAuthorization Error: \((error as NSError).userInfo)")
        }
        isPerformingModalReqest = false
    }
    
    // TODO: Combine credentialAttestationDescriptor and credentialAssertionDescriptor into one generic function
    // About: This function is used to parse <excludeCredentials> during registraation
    // Params: [AttestationOptionsResponse.Credentials]
    // Returns: [ASAuthorizationSecurityKeyPublicKeyCredentialDescriptor]
    // Iterates the public key credential array from the RP and creates an ASAuthorizationSecurityKeyPublicKeyCredentialDescriptor
    // Currently hard-coding the transport for all public key credentials to "allsupported""', which includes ble, usb, and nfc
    // https://developer.apple.com/documentation/authenticationservices/asauthorizationsecuritykeypublickeycredentialdescriptor
    func credentialAttestationDescriptor(credentials: [AttestationOptionsResponse.Credentials]) -> [ASAuthorizationSecurityKeyPublicKeyCredentialDescriptor]? {
        if (credentials.isEmpty) { return nil }
        
        var publicKeyCredentials = [ASAuthorizationSecurityKeyPublicKeyCredentialDescriptor]()
        
        for creds in credentials {
            let cred = ASAuthorizationSecurityKeyPublicKeyCredentialDescriptor.init(credentialID: Data(base64urlEncoded: creds.id)!, transports: ASAuthorizationSecurityKeyPublicKeyCredentialDescriptor.Transport.allSupported)
            
            publicKeyCredentials.append(cred)
        }
        return publicKeyCredentials
    }
    
    
    // About: This function is used to parse <allowCredentials> during authentication
    // Params: [AssertionOptionsResponse.Credentials]
    // Returns: [ASAuthorizationSecurityKeyPublicKeyCredentialDescriptor]
    // Iterates the public key credential array from the RP and creates an ASAuthorizationSecurityKeyPublicKeyCredentialDescriptor
    // Currently hard-coding the transport for all public key credentials to "allsupported"', which includes ble, usb, and nfc
    // https://developer.apple.com/documentation/authenticationservices/asauthorizationsecuritykeypublickeycredentialdescriptor
    func credentialAssertionDescriptor(credentials: [AssertionOptionsResponse.Credentials]) -> [ASAuthorizationSecurityKeyPublicKeyCredentialDescriptor]? {
        
        if (credentials.isEmpty) { return nil }
        
        var publicKeyCredentials = [ASAuthorizationSecurityKeyPublicKeyCredentialDescriptor]()
        
        for creds in credentials {
            let cred = ASAuthorizationSecurityKeyPublicKeyCredentialDescriptor.init(credentialID: Data(base64urlEncoded: creds.id)!, transports: ASAuthorizationSecurityKeyPublicKeyCredentialDescriptor.Transport.allSupported)
            
            publicKeyCredentials.append(cred)
        }
        return publicKeyCredentials
    }
    
    // Get supported public key algorithms from RP and map/build into array of ASAuthorizationPublicKeyCredentialParameters
    func getPublicKeyAlgorithms(_ algorithms: [AttestationOptionsResponse.PublicKeyCredParams]) -> [ASAuthorizationPublicKeyCredentialParameters] {
        
        var publicKeyParams:[ASAuthorizationPublicKeyCredentialParameters] = []
        
        for algo in algorithms {
            publicKeyParams.append(ASAuthorizationPublicKeyCredentialParameters(algorithm: ASCOSEAlgorithmIdentifier(rawValue: algo.alg)))
        }
        return publicKeyParams
    }

    func presentationAnchor(for controller: ASAuthorizationController) -> ASPresentationAnchor {
        return authenticationAnchor!
    }
    
    // MARK: - Security Key Attestation Preferences
    
    // Parse the relying party's attestation statement preference response and return a ASAuthorizationPublicKeyCredentialAttestationKind
    // Acceptable values: direct, indirect, or enterprise
    func attestationStatementPreference(_ rpAttestationStatementPreference: String) -> ASAuthorizationPublicKeyCredentialAttestationKind {
        
        switch rpAttestationStatementPreference {
            case "direct":
                return ASAuthorizationPublicKeyCredentialAttestationKind.direct
            case "indirect":
                return ASAuthorizationPublicKeyCredentialAttestationKind.indirect
            case "enterprise":
                return ASAuthorizationPublicKeyCredentialAttestationKind.enterprise
            default:
                return ASAuthorizationPublicKeyCredentialAttestationKind.direct
        }
    }
    
    // Parse the relying party user verification preference response and return a ASAuthorizationPublicKeyCredentialUserVerificationPreference
    // Acceptable UV preferences: discouraged, preferred, or required
    func userVerificationPreference(_ userVerificationPreference: String) -> ASAuthorizationPublicKeyCredentialUserVerificationPreference {
        
        switch userVerificationPreference {
            case "discouraged":
                return ASAuthorizationPublicKeyCredentialUserVerificationPreference.discouraged
            case "preferred":
                return ASAuthorizationPublicKeyCredentialUserVerificationPreference.preferred
            case "required":
                return ASAuthorizationPublicKeyCredentialUserVerificationPreference.required
            default:
                return ASAuthorizationPublicKeyCredentialUserVerificationPreference.preferred
        }
    }

    // Parse the relying party's resident credential (aka "discoverable credential") preference response and return a ASAuthorizationPublicKeyCredentialResidentKeyPreference
    // Acceptable UV preferences: discouraged, preferred, or required
    func residentKeyPreference(_ residentCredPreference: String) -> ASAuthorizationPublicKeyCredentialResidentKeyPreference {
        
        switch residentCredPreference {
            case "discouraged":
                return ASAuthorizationPublicKeyCredentialResidentKeyPreference.discouraged
            case "preferred":
                return ASAuthorizationPublicKeyCredentialResidentKeyPreference.preferred
            case "required":
                return ASAuthorizationPublicKeyCredentialResidentKeyPreference.required
            default:
                return ASAuthorizationPublicKeyCredentialResidentKeyPreference.preferred
        }
    }
    
    // MARK: - Notifications
    
    func didFinishSignIn() {
        NotificationCenter.default.post(name: .UserSignedIn, object: nil)
    }
    
    func didFailSignIn() {
        NotificationCenter.default.post(name: .UserFailedSignIn, object: nil)
    }

    func didCancelModalSheet() {
        NotificationCenter.default.post(name: .ModalSignInSheetCanceled, object: nil)
    }
}

// MARK: - Extensions

extension String {
    // Encode a string to Base64 encoded string
    // Convert the string to data, then encode the data with base64EncodedString()
    func base64Encoded() -> String? {
        data(using: .utf8)?.base64EncodedString()
    }

    // Decode a Base64 string
    // Convert it to data, then create a string from the decoded data
    func base64Decoded() -> String? {
        guard let data = Data(base64Encoded: self) else { return nil }
        return String(data: data, encoding: .utf8)
    }
}

public extension Data {
    init?(base64urlEncoded input: String) {
        var base64 = input
        base64 = base64.replacingOccurrences(of: "-", with: "+")
        base64 = base64.replacingOccurrences(of: "_", with: "/")
        while base64.count % 4 != 0 {
            base64 = base64.appending("=")
        }
        self.init(base64Encoded: base64)
    }

    func toBase64urlEncodedString() -> String {
        var result = self.base64EncodedString()
        result = result.replacingOccurrences(of: "+", with: "-")
        result = result.replacingOccurrences(of: "/", with: "_")
        result = result.replacingOccurrences(of: "=", with: "")
        return result
    }
}

