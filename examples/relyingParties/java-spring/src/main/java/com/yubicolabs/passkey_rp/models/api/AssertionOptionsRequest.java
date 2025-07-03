package com.yubicolabs.passkey_rp.models.api;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AssertionOptionsRequest
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AssertionOptionsRequest {
  private String userName;
  private Optional<String[]> hints = Optional.ofNullable(null);
}