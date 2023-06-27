package org.openapitools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.yubicolabs.bank_app.models.common.Account;
import com.yubicolabs.bank_app.models.common.AccountTransaction;
import com.yubicolabs.bank_app.services.storage.local.AccountStorage_Local;

@SpringBootTest
class OpenApiGeneratorApplicationTests {

    @Test
    void contextLoads() {
        AccountStorage_Local local = new AccountStorage_Local();

        Account account1 = Account.builder().userHandle("my_user_handle").advancedProtection(false)
                .balance(3000).id(Long.valueOf(123456789)).build();

        assertTrue(local.create(account1));
        assertEquals(local.getAll("my_user_handle").size(), 1);
        System.out
                .println(local.getAll("my_user_handle").stream().findFirst().get().getId() == 123456789);
        assertTrue(local.get(123456789).isPresent());

        System.out.println("Current AP: " + local.get(123456789).get().isAdvancedProtection());
        assertTrue(local.setAdvancedProtection(123456789, true));
        System.out.println("Current AP: " + local.get(123456789).get().isAdvancedProtection());

        System.out.println("Current balance: " + local.get(123456789).get().getBalance());
        AccountTransaction transaction1 = AccountTransaction.builder()
                .type("withdraw")
                .amount(1000)
                .description("To myself")
                .status(true)
                .accountId(123456789).build();
        assertTrue(local.processTransaction(transaction1));
        System.out.println("Current balance: " + local.get(123456789).get().getBalance());

    }

}