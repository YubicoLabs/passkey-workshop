# PawsKey
PawsKey is native iOS Swift app demonstrating the use of Apple's native WebAuthn API implementation in the [Public-Private Key Authentication API Collection](https://developer.apple.com/documentation/authenticationservices/public-private_key_authentication). This API collection supports:
1. [passkeys](https://developer.apple.com/documentation/authenticationservices/public-private_key_authentication/supporting_passkeys) (Touch ID and Face ID)
2. [security keys](https://developer.apple.com/documentation/authenticationservices/public-private_key_authentication/supporting_security_key_authentication_using_physical_keys) (Physical keys, such as NFC, USB, and Lightning connected YubiKeys)

The app is based on Apple's WWDC2021/22 "Shiny" app built for showcasing passkey support in iOS 15/16.

# Objective
Build upon this app to allow users to authenticate using passkeys (Touch & Face ID) and/or security keys (external physical USB, NFC, or Lightning connected keys). 

# Setup

1. You must have an associated domain with the webcredentials service type when making a registration or assertion (authentication) request; See Supporting [associated domains](https://developer.apple.com/documentation/xcode/supporting-associated-domains) for more information

2. You need to have a WebAuthn Relying Party endpoint (backend RESTful API is best) for this app to register and assert passkeys or physical keys

3. Clone this repository

4. Change the relying party endpoint API in the `AccountManager.swift` class to match your own endpoint

6. Build/Run app from Xcode on a physical iOS/iPadOS device 

# Demo
TBD


