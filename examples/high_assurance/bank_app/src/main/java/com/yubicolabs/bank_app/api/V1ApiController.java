package com.yubicolabs.bank_app.api;

import com.yubicolabs.bank_app.models.api.AccountDetailsResponse;
import com.yubicolabs.bank_app.models.api.AccountTransactionListResponse;
import com.yubicolabs.bank_app.models.api.AdvancedProtectionStatusResponse;
import com.yubicolabs.bank_app.models.api.CreateAccountRequest;
import com.yubicolabs.bank_app.models.api.CreateAccountResponse;
import com.yubicolabs.bank_app.models.api.Error;
import com.yubicolabs.bank_app.models.api.TransactionCreateRequest;
import com.yubicolabs.bank_app.models.api.TransactionCreateResponse;
import com.yubicolabs.bank_app.models.api.UpdateAdvancedProtectionStatusRequest;
import com.yubicolabs.bank_app.models.api.UpdateAdvancedProtectionStatusResponse;


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

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-06-27T11:32:42.412827-05:00[America/Chicago]")
@Controller
@RequestMapping("${openapi.passkeyWebAuthnHighAssuranceAPIByYubico.base-path:}")
public class V1ApiController implements V1Api {

    private final NativeWebRequest request;

    @Autowired
    public V1ApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}
