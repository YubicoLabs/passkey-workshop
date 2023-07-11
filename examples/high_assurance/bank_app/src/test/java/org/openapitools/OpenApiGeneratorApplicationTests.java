package org.openapitools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.yubicolabs.bank_app.models.api.AccountTransactionListResponse;
import com.yubicolabs.bank_app.models.api.AdvancedProtectionStatusResponse;
import com.yubicolabs.bank_app.models.api.CreateAccountResponse;
import com.yubicolabs.bank_app.models.api.TransactionCreateResponse;
import com.yubicolabs.bank_app.models.api.UpdateAdvancedProtectionStatusResponse;
import com.yubicolabs.bank_app.models.common.Account;
import com.yubicolabs.bank_app.models.common.AccountTransaction;
import com.yubicolabs.bank_app.services.bank.BankOperations;
import com.yubicolabs.bank_app.services.storage.local.AccountStorage_Local;
import com.yubicolabs.bank_app.services.storage.local.AccountTransactionStorage_Local;

@SpringBootTest
@ActiveProfiles("test")
class OpenApiGeneratorApplicationTests {

    @Autowired
    BankOperations bankOperations;

    @Test
    void testAccountCreation() {
        String userhandle = "user_handle_1";
        try {
            CreateAccountResponse response = bankOperations.createAccount(userhandle);
            assertEquals(response.getStatus(), "created");
        } catch (Exception e) {
            fail("Error creating first account");
        }
        assertThrows(Exception.class, () -> bankOperations.createAccount(userhandle));
    }

    void testAccountNumber() {
        String userhanlde = "user_handle_1";
        // @TODO implement this test
    }

    @Test
    void testAdvancedProtectionGet() {
        try {
            String userhandle = "user_handle_3";
            bankOperations.createAccount(userhandle);
            int accountId = bankOperations.getAccountsByUserhandle(userhandle).getAccounts().get(0).getAccountId();
            try {

                AdvancedProtectionStatusResponse method_response = bankOperations.getAdvancedProtectionStatus(accountId,
                        userhandle);
                assertFalse(method_response.getEnabled());
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
            String wrong_userhandle = "user_handle_2";
            assertThrows(Exception.class,
                    () -> bankOperations.getAdvancedProtectionStatus(accountId, wrong_userhandle));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testAdvancedProtectionSet() {
        try {
            String userhandle = "user_handle_4";
            bankOperations.createAccount(userhandle);
            int accountId = bankOperations.getAccountsByUserhandle(userhandle).getAccounts().get(0).getAccountId();

            try {
                UpdateAdvancedProtectionStatusResponse response = bankOperations.updateAdvancedProtection(accountId,
                        true,
                        userhandle);
                assertTrue(response.getEnabled());

                AdvancedProtectionStatusResponse response_2 = bankOperations.getAdvancedProtectionStatus(accountId,
                        userhandle);
                assertTrue(response_2.getEnabled());
            } catch (Exception e) {
                fail();
            }
            String wrong_userhandle = "user_handle_2";
            assertThrows(Exception.class,
                    () -> bankOperations.updateAdvancedProtection(accountId, false, wrong_userhandle));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testCreateTransaction() {
        try {
            String userhandle = "user_handle_5";
            bankOperations.createAccount(userhandle);
            int accountId = bankOperations.getAccountsByUserhandle(userhandle).getAccounts().get(0).getAccountId();

            try {
                TransactionCreateResponse response = bankOperations.createTransaction("deposit", 1000, "Test",
                        userhandle);
                assertTrue(response.getStatus().equals("complete"));

                assertEquals(bankOperations.getAccountById(accountId, userhandle).getBalance(),
                        BigDecimal.valueOf(4000.0));

                TransactionCreateResponse response_withdraw = bankOperations.createTransaction("withdraw", 1000, "Test",
                        userhandle);
                assertTrue(response_withdraw.getStatus().equals("complete"));

                assertEquals(bankOperations.getAccountById(accountId, userhandle).getBalance(),
                        BigDecimal.valueOf(3000.0));

                TransactionCreateResponse response_cent = bankOperations.createTransaction("withdraw", .52, "Test",
                        userhandle);
                assertTrue(response_cent.getStatus().equals("complete"));

                assertEquals(bankOperations.getAccountById(accountId, userhandle).getBalance(),
                        BigDecimal.valueOf(2999.48));

            } catch (Exception e) {
                fail();
            }
            String wrong_userhandle = "user_handle_2";
            assertThrows(Exception.class, () -> bankOperations.createTransaction("withdraw", 1000, "Test",
                    wrong_userhandle));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testGetTransactions() {
        try {
            String userhandle = "user_handle_6";
            bankOperations.createAccount(userhandle);
            int accountId = bankOperations.getAccountsByUserhandle(userhandle).getAccounts().get(0).getAccountId();

            try {
                bankOperations.createTransaction("deposit", 1000, "Test", userhandle);
                bankOperations.createTransaction("deposit", 1000, "Test", userhandle);
                bankOperations.createTransaction("deposit", 1000, "Test", userhandle);

                AccountTransactionListResponse response = bankOperations.getTransactionsByAccount(accountId,
                        userhandle);
                assertEquals(response.getTransactions().size(), 3);

            } catch (Exception e) {
                fail();
            }

            String wrong_userhandle = "user_handle_2";
            assertThrows(Exception.class, () -> bankOperations.getTransactionsByAccount(accountId, wrong_userhandle));
        } catch (Exception e) {
            fail();
        }

    }

}