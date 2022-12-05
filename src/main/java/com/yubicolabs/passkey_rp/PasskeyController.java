package com.yubicolabs.passkey_rp;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yubico.internal.util.JacksonCodecs;
import com.yubicolabs.passkey_rp.data.AuthenticationResponse;
import com.yubicolabs.passkey_rp.data.RegistrationResponse;

@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class PasskeyController {

  @Autowired
  PasskeyOperations passkeyOperations;
  private final ObjectMapper jsonMapper = JacksonCodecs.json();

  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  @PostMapping("/passkey/register/start")
  public ResponseEntity startRegistration(@RequestBody String request) {
    JsonObject obj = JsonParser.parseString(request).getAsJsonObject();
    try {
      return ResponseEntity.status(HttpStatus.OK)
          .body(passkeyOperations.startRegistration(obj));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(e);
    }
  }

  @PostMapping("/passkey/register/finish")
  public ResponseEntity finishRegistration(@RequestBody String request) {
    try {
      RegistrationResponse response = jsonMapper.readValue(request, RegistrationResponse.class);

      return ResponseEntity.status(HttpStatus.OK)
          .body(passkeyOperations.finishRegistration(response));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(e);
    }
  }

  @GetMapping("/passkey/authenticate/start")
  public ResponseEntity startAuthentication(@RequestHeader Map<String, String> header) {
    try {
      header.forEach((key, value) -> {
        System.out.println("Header " + key + " = " + value);
      });
      return ResponseEntity.status(HttpStatus.OK)
          .body(passkeyOperations.startAuthentication());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(e);
    }
  }

  @PostMapping("/passkey/authenticate/finish")
  public ResponseEntity finishAuthentication(@RequestBody String request) {
    try {
      AuthenticationResponse response = jsonMapper.readValue(request, AuthenticationResponse.class);

      return ResponseEntity.status(HttpStatus.OK)
          .body(passkeyOperations.finishAuthentication(response));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(e);
    }
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
