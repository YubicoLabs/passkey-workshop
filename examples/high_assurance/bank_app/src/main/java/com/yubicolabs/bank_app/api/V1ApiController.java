package com.yubicolabs.bank_app.api;

import com.yubicolabs.bank_app.models.api.CreateAccountRequest;
import com.yubicolabs.bank_app.models.api.Error;
import com.yubicolabs.bank_app.models.api.TransactionCreateRequest;
import com.yubicolabs.bank_app.models.api.UpdateAdvancedProtectionStatusRequest;
import com.yubicolabs.bank_app.services.bank.BankOperations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;
import javax.annotation.Generated;
import javax.security.sasl.AuthenticationException;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-06-30T13:35:17.173429-05:00[America/Chicago]")
@Controller
@RequestMapping("${openapi.passkeyWebAuthnHighAssuranceAPIByYubico.base-path:}")
public class V1ApiController implements V1Api {

    private final NativeWebRequest request;

    @Autowired
    BankOperations bankOperations;

    @Autowired
    public V1ApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity accountDetails(
            Integer accountId) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(bankOperations.getAccountById(accountId.intValue(), getUserHandleFromJwt()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Error.builder().status("error").errorMessage(e.getMessage()).build());
        }
    }

    @Override
    public ResponseEntity accountTransactionList(
            Integer accountId) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(bankOperations.getTransactionsByAccount(accountId.intValue(), getUserHandleFromJwt()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Error.builder().status("error").errorMessage(e.getMessage()).build());
        }
    }

    @Override
    public ResponseEntity advancedProtectionStatus(
            Integer accountId) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(bankOperations.getAdvancedProtectionStatus(accountId.intValue(), getUserHandleFromJwt()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Error.builder().status("error").errorMessage(e.getMessage()).build());
        }
    }

    @Override
    public ResponseEntity createAccountRequest(CreateAccountRequest body) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(bankOperations.createAccount(body.getUserHandle()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Error.builder().status("error").errorMessage(e.getMessage()).build());
        }
    }

    @Override
    public ResponseEntity getAccountsRequest() {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(bankOperations.getAccountsByUserhandle(getUserHandleFromJwt()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Error.builder().status("error").errorMessage(e.getMessage()).build());
        }
    }

    @Override
    public ResponseEntity transactionCreate(Integer accountId, TransactionCreateRequest transactionCreateRequest) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(bankOperations.createTransaction(getAcr(), transactionCreateRequest.getType().getValue(),
                            transactionCreateRequest.getAmount().doubleValue(),
                            transactionCreateRequest.getDescription(), getUserHandleFromJwt()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Error.builder().status("error").errorMessage(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Error.builder().status("error").errorMessage(e.getMessage()).build());
        }
    }

    @Override
    public ResponseEntity updateAdvancedProtectionStatus(Integer accountId,
            UpdateAdvancedProtectionStatusRequest updateAdvancedProtectionStatusRequest) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(bankOperations.updateAdvancedProtection(accountId.intValue(),
                            updateAdvancedProtectionStatusRequest.getEnabled(), getUserHandleFromJwt()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Error.builder().status("error").errorMessage(e.getMessage()).build());
        }
    }

    private String getUserHandleFromJwt() {
        JwtAuthenticationToken token = (JwtAuthenticationToken) SecurityContextHolder.getContext()
                .getAuthentication();
        Jwt jwt = (Jwt) token.getCredentials();
        return (String) jwt.getClaims().get("sub");
    }

    private int getAcr() {
        JwtAuthenticationToken token = (JwtAuthenticationToken) SecurityContextHolder.getContext()
                .getAuthentication();
        Jwt jwt = (Jwt) token.getCredentials();
        return Integer.parseInt((String) jwt.getClaims().get("acr"));
    }

}
