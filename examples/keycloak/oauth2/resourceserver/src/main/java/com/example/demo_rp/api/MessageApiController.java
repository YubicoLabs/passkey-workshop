package com.example.demo_rp.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-10T11:25:48.009047+02:00[Europe/Amsterdam]")
@Controller
@RequestMapping("${openapi.apiTest.base-path:}")
public class MessageApiController implements MessageApi {

    private final NativeWebRequest request;

    @Autowired
    public MessageApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<Void> messageGet() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }
 
    @Override
    public ResponseEntity<Void> messagePost() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
