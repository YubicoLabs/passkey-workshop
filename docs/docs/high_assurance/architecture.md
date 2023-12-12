---
sidebar_position: 3
---

# Architecture

Our consumer bank scenario is illustrated by a sample application that will require step-up authentication for "high-risk" transactions.
This section provides an overview of the components used in this application, and how they interact with one another.

## Components

We can distinguish the following six components:

1. The Banking client. Strictly speaking, these are two components, as we will be using both a web client and a mobile client. The Web client is implemented using React. The mobile client is a native iOS app.
2. The Banking API. This is a server component hosting a banking API, used by both clients to perform transactions such as money transfers between accounts.
3. The Identity Provider. The is a separate server component that is used in conjunction with the API server. It is used to host user identities and issue tokens to secure the banking API.
4. The WebAuthn server. This is the same component as used before in the demo application, but configured slightly differently in order to support the use of attestation and  multiple assurance levels.
5. The Database server, used as before by the WebAuthn server to store passkey registration and authentication data, as well as used by the banking API server to store bank account information.
6. Lastly, the FIDO Metadata service is used by the WebAuthn server for obtaining attestation data to assess the assurance level of different authenticators. Note that this is a cloud service so we do not need to deploy this service locally.

These components and their interconnections are illustrated in the following diagram:

![Components](/img/architecture-components.png)

@TODO: redraw image so that KeyCloak also has access to both the database and the webauthn server

In this section we will focus on the first three components as they interact in a specific way to implement our high assurance use case using step-up authentication.

Refer to the following diagram:

![Architecture](/img/architecture.png)

The server components have a different role:

- The Bank API is used for client access to a user's bank account
- The Bank Authorization Server is used to authenticate users and authorize access to the bank API.

## Authentication and Authorization

As with the demo application, an OpenID Connect Provider is used to host user identities. 
It is used by the web and mobile clients to authenticate users.
After a user successfully authenticates, the clients are issued an ID Token containing information on the user's identity together with details on the authentication event.
To learn more about OpenID Connect, see [openid.net/developers](https://openid.net/developers/).

After authentication, the clients need to interact with the banking API server to access their account.
To authorize the client to access a bank account on behalf of the user, the [OAuth 2.0](https://oauth.net/2/) protocol is used.

OAuth2 works with access tokens to protect APIs.
When a user is authenticated and an ID Token is issued to their client,
the client also receives an access token that can be used to access the banking API.
The banking API requires this access token whenever a client wants to access a user's bank account.
The access token is bound to the user that authorized the issuance of the token, and has a limited validity period.
The banking API will validate the access token and only perform actions on the user's account if the token is valid.

:::::danger A note on Relying Parties
Beware that both the WebAuthn and the OpenID Connect specs use the term Relying Party.
In OpenID Connect a Relying Party is the service that outsources user authentication to an Identity Provider (the OpenID Provider).
In WebAuthn a Relying Party is the web application that uses WebAuthn to register and authenticate users.
In our banking application, we use KeyCloak to authenticate users using passkeys.
Strictly speaking, the banking application is the OpenID Relying Party, but the OpenID Provider is the WebAuthn Relying Party.
Here, we will not distinguish the two as the OpenID Provider and Banking application are closely connected .
:::::

This user flow is depicted in the diagram below: 

![Login Flow](/img/architecture-login-flow.png)

1. The client application initially does not have an access token, so cannot yet access the Bank API.
To obtain a token, the client is first redirected to the bank's OAuth 2.0 Authorization Server (AS) with a request for an access token.
The Authorization Server first needs to authenticate the user in order to know which user to issue an access token for.
In our case, the Authorization Server is also the OpenID Provider, and the user is authenticated with their passkey.
2. When authentication is successful, an access token is issued for that user and returned to the client.
3. The client now calls the banking API to access the user's account. Along with the API call, the access token is sent to authorize the call.
4. The banking API validates the access token, and if successful, performs the API call and returns any data as a result of the call to the client.

:::::info OAuth 2.0 flows
Note that we slightly simplified the diagram.
In reality, obtaining an access token is slightly more complicated.
This is because the access token is returned over a so called back-channel, instead of a front-channel (as the diagram suggests using redirects).
This is called the *authorization code* flow in OAuth 2.0.
:::::

## Step-up Authentication

Now let's have a look at what happens when a user has authenticated with a copyable passkey (which is assigned a low assurance level),
but tries to make an API call that requires a high assurance level.

When calling the API, sending along the obtained access token, the API server will validate the access token before performing the API call.
let's assume the access token hasn't expired.
But because the API call requires high assurance, the API server will inspect some data associated with the access token.
This data is called the *authentication context class reference*, or ACR, and its value is set by the entity that authenticated the user, 
indicating how authentication was performed. In out case, it indicates the assurance level of the associated passkey.

Because the user authenticated using low assurance, the API server will deny the API request, and send back an error message indicating the required assurance level.
The client will subsequently return to the Authorization Server to obtain a new access token, but this time specifically requesting to authenticate the user on a high assurance level.
This means the Authorization Server will now require the user to use a passkey stored on a security key.
Assuming the user has registered such a passkey, the user authenticates on a high assurance level and a new access token is issued and returned to the client.
This time, the access token is marked with an ACR value indicating high assurance, so when the API call is retried, the server will grant access and perform the API call as requested.



![Stepup Flow](/img/architecture-stepup.png)

