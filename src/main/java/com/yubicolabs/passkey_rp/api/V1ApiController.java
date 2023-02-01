package com.yubicolabs.passkey_rp.api;

import com.yubicolabs.passkey_rp.models.api.APIStatus;
import com.yubicolabs.passkey_rp.models.api.AssertionOptionsRequest;
import com.yubicolabs.passkey_rp.models.api.AssertionOptionsResponse;
import com.yubicolabs.passkey_rp.models.api.AssertionResultRequest;
import com.yubicolabs.passkey_rp.models.api.AssertionResultResponse;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsRequest;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsResponse;
import com.yubicolabs.passkey_rp.models.api.AttestationResultRequest;
import com.yubicolabs.passkey_rp.models.api.AttestationResultResponse;
import com.yubicolabs.passkey_rp.models.api.Error;
import com.yubicolabs.passkey_rp.models.api.UserCredentialDelete;
import com.yubicolabs.passkey_rp.models.api.UserCredentialDeleteResponse;
import com.yubicolabs.passkey_rp.models.api.UserCredentialUpdate;
import com.yubicolabs.passkey_rp.models.api.UserCredentialUpdateResponse;
import com.yubicolabs.passkey_rp.models.api.UserCredentialsResponse;
import com.yubicolabs.passkey_rp.models.api.UserDelete;
import com.yubicolabs.passkey_rp.models.api.UserDeleteResponse;
import com.yubicolabs.passkey_rp.models.api.UserProfileResponse;
import com.yubicolabs.passkey_rp.services.passkey.PasskeyOperations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.constraints.*;
import javax.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-31T11:58:50.125043-06:00[America/Chicago]")
@Controller
@RequestMapping("${openapi.passkeyWebAuthnAPIByYubico.base-path:}")
@CrossOrigin(origins = "http://localhost:3000")
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
    public ResponseEntity<AttestationOptionsResponse> serverPublicKeyCredentialCreationOptionsRequest(
            AttestationOptionsRequest attestationOptionsRequest) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(passkeyOperations.attestationOptions(attestationOptionsRequest));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<AttestationResultResponse> serverAuthenticatorAttestationResponse(
            AttestationResultRequest attestationOptionsResult) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(passkeyOperations.attestationResult(attestationOptionsResult));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AttestationResultResponse()
                    .status("There was as issue registering your credential: " + e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<AssertionOptionsResponse> serverPublicKeyCredentialGetOptionsRequest(
            AssertionOptionsRequest assertionOptionsRequest) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(passkeyOperations.assertionOptions(assertionOptionsRequest));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AssertionOptionsResponse.builder()
                    .errorMessage("There was as issue registering your credential: " + e.getMessage()).build());
        }
    }

    @Override
    public ResponseEntity<UserCredentialsResponse> userCredentialsByID(String userName) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(passkeyOperations.getUserCredentials(userName));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<AssertionResultResponse> serverAuthenticatorAssertionResponse(
            AssertionResultRequest assertionResultRequest) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(passkeyOperations.assertionResponse(assertionResultRequest));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
