package com.yubicolabs.passkey_rp.api;

import com.yubicolabs.passkey_rp.models.api.AssertionOptionsRequest;
import com.yubicolabs.passkey_rp.models.api.AssertionResultRequest;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsRequest;
import com.yubicolabs.passkey_rp.models.api.AttestationResultRequest;
import com.yubicolabs.passkey_rp.models.api.Error;
import com.yubicolabs.passkey_rp.models.api.UpdateAdvancedProtectionStatusRequest;
import com.yubicolabs.passkey_rp.models.api.UserCredentialDelete;
import com.yubicolabs.passkey_rp.models.api.UserCredentialUpdate;
import com.yubicolabs.passkey_rp.services.passkey.PasskeyOperations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-31T11:58:50.125043-06:00[America/Chicago]")
@Controller
@RequestMapping("${openapi.passkeyWebAuthnAPIByYubico.base-path:}")
public class V1ApiController implements V1Api {

    private final NativeWebRequest request;

    @Autowired
    PasskeyOperations passkeyOperations;

    @Autowired
    public V1ApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity serverPublicKeyCredentialCreationOptionsRequest(
            AttestationOptionsRequest attestationOptionsRequest) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(passkeyOperations.attestationOptions(attestationOptionsRequest));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Error.builder().status("error").errorMessage(e.getMessage()).build());
        }
    }

    @Override
    public ResponseEntity serverAuthenticatorAttestationResponse(
            AttestationResultRequest attestationOptionsResult) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(passkeyOperations.attestationResult(attestationOptionsResult));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Error.builder().status("error").errorMessage(e.getMessage()).build());
        }
    }

    @Override
    public ResponseEntity serverPublicKeyCredentialGetOptionsRequest(
            AssertionOptionsRequest assertionOptionsRequest) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(passkeyOperations.assertionOptions(assertionOptionsRequest));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Error.builder().status("error").errorMessage(e.getMessage()).build());
        }
    }

    @Override
    public ResponseEntity userCredentialsByID(String userName) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(passkeyOperations.getUserCredentials(userName));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Error.builder().status("error").errorMessage(e.getMessage()).build());
        }
    }

    @Override
    public ResponseEntity serverAuthenticatorAssertionResponse(
            AssertionResultRequest assertionResultRequest) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(passkeyOperations.assertionResponse(assertionResultRequest));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Error.builder().status("error").errorMessage(e.getMessage()).build());
        }
    }

    @Override
    @Transactional
    public ResponseEntity userCredentialDelete(
            UserCredentialDelete userCredentialDelete) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(passkeyOperations.deleteCredential(userCredentialDelete));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Error.builder().status("error").errorMessage(e.getMessage()).build());
        }
    }

    @Override
    @Transactional
    public ResponseEntity userCredentialUpdate(
            UserCredentialUpdate userCredentialUpdate) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(passkeyOperations.updateCredentialNickname(userCredentialUpdate));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Error.builder().status("error").errorMessage(e.getMessage()).build());
        }
    }

    @Override
    public ResponseEntity advancedProtectionStatus(String userHandle) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(passkeyOperations.getAdvancedProtectionStatus(userHandle));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Error.builder().status("error").errorMessage(e.getMessage()).build());
        }
    }

    @Override
    public ResponseEntity updateAdvancedProtectionStatus(String userHandle,
            UpdateAdvancedProtectionStatusRequest updateAdvancedProtectionStatusRequest) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(passkeyOperations.updateAdvancedProtectionStatus(userHandle,
                            updateAdvancedProtectionStatusRequest));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Error.builder().status("error").errorMessage(e.getMessage()).build());
        }
    }

}
