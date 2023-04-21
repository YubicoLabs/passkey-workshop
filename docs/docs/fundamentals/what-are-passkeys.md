---
sidebar_position: 1
---

# What are passkeys?

Passkeys are an easier to use and more secure alternative to passwords.
This section explains why.

## Passkeys are easier to use than passwords

With passkeys, users no longer need to remember a secret password for every account.
Instead, a cryptographic credential is automatically generated when signing up for a new account. 
Later, when signing back in to the account, users can sign in with their face, their finger, or their PIN.

Optionally, passkeys can be automatically backed up and synced across devices. This means there is no need to generate backup codes and it becomes less complicated to recover an account after a device is lost or stolen.

## Passkeys are more secure than passwords

Passkeys are more secure for several reasons. Let's review the most importent ones:

To start with: unlike passwords, passkeys have built-in protection against phishing-attacks.
This means that if a user accidentally clicks a link to a fake website, there is no risk of the user entering credentials in the wrong place, compromising their account.
A passkey simply won't work on a website with an address other than the one the passkey was registered for.

Secondly, passkeys do not contain shared secrets, which means there are no secrets stored on any servers then can be stolen and subsequently used by adversaries to hijack accounts.
Instead, passkeys make use of 
[Public Key cryptography](https://en.wikipedia.org/wiki/Public-key_cryptography), where only public information needs to be stored on the server.

Passkeys support [Multi-Factor Authentication](https://en.wikipedia.org/wiki/Multi-factor_authentication) (MFA).
This means that when a user authenticates to an application, multiple authentication **factors** are required. A factor can be something your _know_ (such as a password or a PIN), something you _have_ (such as a USB device or a phone), and something you _are_ (a biometric such as a fingerprint or a face scan).
Passkeys typically combine a posession factor with either a knowledge factor or a biometric to achieve MFA.

:::tip Password versus PIN

You may think that a PIN is similar to a password, but in the context of passkeys there is an important difference:
A password is used to authenticate to a remote application, whereas a PIN is used to authenticate to a local device.
Examples are the unlock code of your phone or the PIN of a smart card: they are never sent to a remote server.
:::

Lastly, passkeys are generated automatically. This means that they are never reused, so it is guaranteed that users have unique passkeys for all their accounts.

## Storing passkeys

So, with passkeys users no longer need to remember their account password. The question is: if passkeys are not "stored" in a user's memory, where are passkeys stored?
The answer is: in an __authenticator__