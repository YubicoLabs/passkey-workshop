package com.yubicolabs.passkey_rp.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yubicolabs.passkey_rp.models.api.AdvancedProtection;
import com.yubicolabs.passkey_rp.models.api.AssertionOptionsRequest;
import com.yubicolabs.passkey_rp.models.api.AssertionOptionsResponse;
import com.yubicolabs.passkey_rp.models.api.AssertionResultRequest;
import com.yubicolabs.passkey_rp.models.api.AssertionResultResponse;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsRequest;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsResponse;
import com.yubicolabs.passkey_rp.models.api.AttestationResultRequest;
import com.yubicolabs.passkey_rp.models.api.AttestationResultResponse;
import com.yubicolabs.passkey_rp.models.api.UpdateAdvancedProtectionStatusRequest;
import com.yubicolabs.passkey_rp.models.api.UserCredentialDelete;
import com.yubicolabs.passkey_rp.models.api.UserCredentialDeleteResponse;
import com.yubicolabs.passkey_rp.models.api.UserCredentialUpdate;
import com.yubicolabs.passkey_rp.models.api.UserCredentialUpdateResponse;
import com.yubicolabs.passkey_rp.models.api.UserCredentialsResponse;
import com.yubicolabs.passkey_rp.services.passkey.PasskeyOperations;

@RestController
@RequestMapping("/v1")
public class FidoController {

  private final PasskeyOperations passkeyOperations;

  public FidoController(final PasskeyOperations passkeyOperations) {
    this.passkeyOperations = passkeyOperations;
  }

  @RequestMapping(method = RequestMethod.POST, value = "/attestation/options", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AttestationOptionsResponse> attestationOptions(
      @RequestBody AttestationOptionsRequest attestationOptionsRequest) {

    final AttestationOptionsResponse response = passkeyOperations.attestationOptions(attestationOptionsRequest);

    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(response);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/attestation/result", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AttestationResultResponse> attestationResult(
      @RequestBody AttestationResultRequest attestationResultRequest) {
    final AttestationResultResponse response = passkeyOperations.attestationResult(attestationResultRequest);

    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(response);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/assertion/options", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AssertionOptionsResponse> assertionOptions(
      @RequestBody AssertionOptionsRequest assertionOptionsRequest) {
    final AssertionOptionsResponse response = passkeyOperations.assertionOptions(assertionOptionsRequest);

    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(response);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/assertion/result", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AssertionResultResponse> assertionResult(
      @RequestBody AssertionResultRequest assertionResultRequest) {
    final AssertionResultResponse response = passkeyOperations.assertionResponse(assertionResultRequest);

    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(response);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/user/credentials/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UserCredentialsResponse> userCredentialsByUsername(@PathVariable String username)
      throws Exception {
    final UserCredentialsResponse response = passkeyOperations.getUserCredentials(username);

    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(response);
  }

  @RequestMapping(method = RequestMethod.PUT, value = "/user/credentials", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UserCredentialUpdateResponse> updateCredentialNicknameById(
      @RequestBody UserCredentialUpdate userCredentialUpdate)
      throws Exception {
    final UserCredentialUpdateResponse response = passkeyOperations.updateCredentialNickname(userCredentialUpdate);

    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(response);
  }

  @RequestMapping(method = RequestMethod.DELETE, value = "/user/credentials", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UserCredentialDeleteResponse> deleteCredentialNicknameById(
      @RequestBody UserCredentialDelete userCredentialDelete)
      throws Exception {
    final UserCredentialDeleteResponse response = passkeyOperations.deleteCredential(userCredentialDelete);

    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(response);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/user/advanced-protection/{userHandle}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdvancedProtection> getAdvancedProtectionStatus(@PathVariable String userHandle)
      throws Exception {
    AdvancedProtection response = passkeyOperations.getAdvancedProtectionStatus(userHandle);

    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(response);
  }

  @RequestMapping(method = RequestMethod.PUT, value = "/user/advanced-protection/{userHandle}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdvancedProtection> updateAdvancedProtectionStatus(@PathVariable String userHandle,
      @RequestBody UpdateAdvancedProtectionStatusRequest updateAdvancedProtectionStatusRequest) throws Exception {
    AdvancedProtection response = passkeyOperations.updateAdvancedProtectionStatus(userHandle,
        updateAdvancedProtectionStatusRequest);

    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(response);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity getApiStatus() {
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON).build();
  }

}
