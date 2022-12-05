package com.yubicolabs.passkey_rp;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yubico.internal.util.JacksonCodecs;
import com.yubicolabs.passkey_rp.data.AuthenticationRequest;
import com.yubicolabs.passkey_rp.data.AuthenticationResponse;
import com.yubicolabs.passkey_rp.data.CredentialRegistration;
import com.yubicolabs.passkey_rp.data.RegistrationResponse;
import com.yubicolabs.passkey_rp.data.StartRegistrationRequest;

@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class PasskeyController {

  @Autowired
  PasskeyOperations passkeyOperations;

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    return JacksonCodecs.json();
  }

  @PostMapping("/passkey/register/start")
  public ResponseEntity startRegistration(@RequestBody StartRegistrationRequest request) throws Exception {
    return ResponseEntity.status(HttpStatus.OK)
        .body(passkeyOperations.startRegistration(request));
  }

  @PostMapping("/passkey/register/finish")
  public ResponseEntity<CredentialRegistration> finishRegistration(@RequestBody RegistrationResponse request)
      throws Exception {

    return ResponseEntity.status(HttpStatus.OK)
        .body(passkeyOperations.finishRegistration(request));
  }

  @GetMapping("/passkey/authenticate/start")
  public ResponseEntity<AuthenticationRequest> startAuthentication(@RequestHeader Map<String, String> header)
      throws Exception {
    header.forEach((key, value) -> {
      System.out.println("Header " + key + " = " + value);
    });
    return ResponseEntity.status(HttpStatus.OK)
        .body(passkeyOperations.startAuthentication());
  }

  @PostMapping("/passkey/authenticate/finish")
  public ResponseEntity finishAuthentication(@RequestBody AuthenticationResponse request) throws Exception {
    return ResponseEntity.status(HttpStatus.OK)
        .body(passkeyOperations.finishAuthentication(request));
  }

  // TODO: Remove this when you are finished
  @GetMapping("/passkey/getrr")
  public ResponseEntity getAllRR() {
    try {
      return ResponseEntity.status(HttpStatus.OK)
          .body(passkeyOperations.relyingPartyInstance.getStorageInstance().getRegistrationRequestStorage().getAll());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(e);
    }
  }

  // TODO: Remove this when you are finished
  @GetMapping("/passkey/getcr")
  public ResponseEntity getAllCR() {
    try {
      return ResponseEntity.status(HttpStatus.OK)
          .body(passkeyOperations.relyingPartyInstance.getStorageInstance().getCredentialStorage().getAll());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(e.getMessage());
    }
  }

  @GetMapping("/passkey")
  public String hello() {
    return "Hello Passkey";
  }
}
