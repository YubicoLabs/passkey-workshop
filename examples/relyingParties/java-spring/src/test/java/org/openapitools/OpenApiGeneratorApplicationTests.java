package org.openapitools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import com.yubico.webauthn.RelyingParty;
import com.yubicolabs.passkey_rp.services.passkey.RelyingPartyInstance;

@SpringBootTest
@ActiveProfiles("test")
class OpenApiGeneratorApplicationTests {

    // Removing test as it requires a live database connection
    @Autowired
    RelyingPartyInstance relyingPartyInstance;

    @Autowired
    Environment env;

    /**
     * Test the RelyingPartyInstance singleton parameters
     */
    @Test
    void testRPInstanceConfigs() {
        /**
         * Test the RP_ID and RP_NAME
         */
        assertEquals(env.getProperty("RP_ID"), relyingPartyInstance.getRelyingParty().getIdentity().getId());
        assertEquals(env.getProperty("RP_NAME"), relyingPartyInstance.getRelyingParty().getIdentity().getName());
    }

}