package com.yubicolabs.passkey_rp.data;

/*
 * Simple key value pair for db storage
 * Used for AssertionRequestStorage and RegistrationRequestStorage
 */
public class SimpleDBO {
  String key;
  String value; // Should resolve to either AuthenticationRequest or RegistrationRequest
}
