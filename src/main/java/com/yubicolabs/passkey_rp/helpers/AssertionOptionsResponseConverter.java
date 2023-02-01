package com.yubicolabs.passkey_rp.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;
import com.yubicolabs.passkey_rp.models.api.AssertionOptionsResponse;
import com.yubicolabs.passkey_rp.models.api.AssertionOptionsResponsePublicKey;
import com.yubicolabs.passkey_rp.models.api.AssertionOptionsResponsePublicKeyAllowCredentialsInner;
import com.yubicolabs.passkey_rp.models.api.AssertionOptionsResponsePublicKey.AssertionOptionsResponsePublicKeyBuilder;
import com.yubicolabs.passkey_rp.models.api.AssertionOptionsResponsePublicKey.UserVerificationEnum;

public class AssertionOptionsResponseConverter {

        public static AssertionOptionsResponse PKCtoResponse(PublicKeyCredentialRequestOptions pkc, ByteArray requestId)
                        throws Exception {
                try {
                        /**
                         * Check if there is an allowed credential list
                         */
                        List<AssertionOptionsResponsePublicKeyAllowCredentialsInner> allowCredentials = new ArrayList<>();

                        if (pkc.getAllowCredentials().isPresent()) {
                                allowCredentials = pkc
                                                .getAllowCredentials().get().stream()
                                                .map(item -> AssertionOptionsResponsePublicKeyAllowCredentialsInner
                                                                .builder()
                                                                .id(item.getId().getBase64Url())
                                                                .type(item.getType().getId())
                                                                .build())
                                                .collect(Collectors.toList());
                        }

                        AssertionOptionsResponsePublicKeyBuilder pkBuilder = AssertionOptionsResponsePublicKey
                                        .builder();

                        /**
                         * Add known values
                         */
                        pkBuilder.challenge(pkc.getChallenge().getBase64Url())
                                        .timeout(pkc.getTimeout().get().intValue())
                                        .rpId(pkc.getRpId())
                                        .userVerification(UserVerificationEnum
                                                        .fromValue(pkc.getUserVerification().get().getValue()));

                        /*
                         * Add allow credentials if present
                         */
                        if (pkc.getAllowCredentials().isPresent()) {
                                pkBuilder.allowCredentials(allowCredentials);
                        }

                        return AssertionOptionsResponse.builder()
                                        .requestId(requestId.getBase64Url())
                                        .publicKey(pkBuilder.build())
                                        .build();
                } catch (Exception e) {
                        e.printStackTrace();
                        throw new Exception(e.getMessage());
                }
        }
}
