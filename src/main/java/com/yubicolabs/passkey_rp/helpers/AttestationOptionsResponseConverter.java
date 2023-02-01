package com.yubicolabs.passkey_rp.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsResponse;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsResponsePublicKey;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsResponsePublicKeyAuthenticatorSelection;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsResponsePublicKeyExcludeCredentialsInner;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsResponsePublicKeyPubKeyCredParamsInner;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsResponsePublicKeyRp;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsResponsePublicKeyUser;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsResponsePublicKey.AttestationEnum;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsResponsePublicKeyAuthenticatorSelection.AttestationOptionsResponsePublicKeyAuthenticatorSelectionBuilder;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsResponsePublicKeyAuthenticatorSelection.AuthenticatorAttachmentEnum;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsResponsePublicKeyAuthenticatorSelection.ResidentKeyEnum;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsResponsePublicKeyAuthenticatorSelection.UserVerificationEnum;

public class AttestationOptionsResponseConverter {

        public static AttestationOptionsResponse PKCtoResponse(PublicKeyCredentialCreationOptions pkc,
                        ByteArray requestID) throws Exception {
                try {
                        AttestationOptionsResponsePublicKeyRp rp = AttestationOptionsResponsePublicKeyRp.builder()
                                        .name(pkc.getRp().getName())
                                        .id(pkc.getRp().getId())
                                        .build();

                        AttestationOptionsResponsePublicKeyUser user = AttestationOptionsResponsePublicKeyUser.builder()
                                        .id(pkc.getUser().getId().getBase64Url())
                                        .name(pkc.getUser().getName())
                                        .displayName(pkc.getUser().getDisplayName())
                                        .build();

                        List<AttestationOptionsResponsePublicKeyPubKeyCredParamsInner> pkcParam = pkc
                                        .getPubKeyCredParams()
                                        .stream()
                                        .map(item -> AttestationOptionsResponsePublicKeyPubKeyCredParamsInner.builder()
                                                        .alg((int) item.getAlg().getId())
                                                        .type(item.getType().getId())
                                                        .build())
                                        .collect(Collectors.toList());

                        List<AttestationOptionsResponsePublicKeyExcludeCredentialsInner> exCred = pkc
                                        .getExcludeCredentials()
                                        .isPresent()
                                                        ? pkc.getExcludeCredentials().get().stream()
                                                                        .map(item -> AttestationOptionsResponsePublicKeyExcludeCredentialsInner
                                                                                        .builder()
                                                                                        .type(item.getType().toString()
                                                                                                        .toLowerCase())
                                                                                        .id(item.getId().getBase64Url())
                                                                                        .build())
                                                                        .collect(Collectors.toList())
                                                        : new ArrayList<AttestationOptionsResponsePublicKeyExcludeCredentialsInner>();

                        /*
                         * When we generate the AuthenticatorSelection object in the pkc, we are going
                         * to be opinionated towards our best practices
                         * This means that every pkc that gets passed into this builder should contain:
                         * * The AuthenticatorSelection object
                         * * A residentKey value
                         * * A UV object
                         * 
                         * The only object that we should verify is the auth attachment, as it may not
                         * be present in all scenarios
                         */

                        AttestationOptionsResponsePublicKeyAuthenticatorSelectionBuilder builder = AttestationOptionsResponsePublicKeyAuthenticatorSelection
                                        .builder();

                        builder.userVerification(UserVerificationEnum.fromValue(
                                        pkc.getAuthenticatorSelection().get().getUserVerification().get().getValue()));
                        builder.residentKey(ResidentKeyEnum.fromValue(
                                        pkc.getAuthenticatorSelection().get().getResidentKey().get().getValue()));

                        if (pkc.getAuthenticatorSelection().get().getAuthenticatorAttachment().isPresent()) {
                                builder.authenticatorAttachment(
                                                AuthenticatorAttachmentEnum.fromValue(pkc.getAuthenticatorSelection()
                                                                .get().getAuthenticatorAttachment().get().getValue()));
                        }

                        AttestationOptionsResponsePublicKey publicKey = AttestationOptionsResponsePublicKey.builder()
                                        .rp(rp)
                                        .user(user)
                                        .challenge(pkc.getChallenge().getBase64Url())
                                        .pubKeyCredParams(pkcParam)
                                        .timeout(pkc.getTimeout().get().intValue())
                                        .excludeCredentials(exCred)
                                        .authenticatorSelection(builder.build())
                                        .attestation(AttestationEnum.fromValue(pkc.getAttestation().getValue()))
                                        .build();

                        return AttestationOptionsResponse.builder()
                                        .requestId(requestID.getBase64Url())
                                        .publicKey(publicKey)
                                        .build();
                } catch (Exception e) {
                        e.printStackTrace();
                        throw new Exception("Issue converting credential: " + e.toString());
                }
        }
}
