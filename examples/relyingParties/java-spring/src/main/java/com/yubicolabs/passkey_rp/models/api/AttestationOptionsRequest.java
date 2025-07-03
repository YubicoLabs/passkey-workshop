package com.yubicolabs.passkey_rp.models.api;

import java.util.Optional;

import com.yubico.webauthn.data.AuthenticatorSelectionCriteria;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AttestationOptionsRequest
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AttestationOptionsRequest {
  private String userName;
  private String displayName;
  @Schema(name = "authenticatorSelection", example = "{\n" + //
      "    \"authenticatorAttachment\": \"cross_platform\",\n" + //
      "    \"residentKey\": \"discouraged\",\n" + //
      "    \"userVerification\": \"discouraged\"\n" + //
      "  }", required = false)
  private AuthenticatorSelectionCriteria authenticatorSelection;
  private Optional<String[]> hints = Optional.ofNullable(null);
}