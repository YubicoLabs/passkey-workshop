package com.yubicolabs.passkey_rp.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.google.gson.JsonObject;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = { Exception.class })
  protected ResponseEntity handleException(Exception e) {
    // BUild error

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
  }
}
