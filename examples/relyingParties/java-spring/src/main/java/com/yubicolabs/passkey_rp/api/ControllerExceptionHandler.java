package com.yubicolabs.passkey_rp.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.yubicolabs.passkey_rp.models.api.Error;

@ControllerAdvice
public class ControllerExceptionHandler {

  @ExceptionHandler(Exception.class)
  private ResponseEntity handleGlobalError(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(MediaType.APPLICATION_JSON).body(e);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity handleException(HttpMessageNotReadableException exception, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(Error.builder().status("error").errorMessage(exception.getMessage()).build());
  }
}
