package com.yubicolabs.passkey_rp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yubico.internal.util.JacksonCodecs;

@Configuration
public class JacksonConfiguration {

  @Bean
  public ObjectMapper ObjectMapper() {
    return JacksonCodecs.json().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

}
