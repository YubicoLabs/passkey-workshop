package org.openapitools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.yubicolabs.bank_app.models.common.Account;
import com.yubicolabs.bank_app.models.common.AccountTransaction;
import com.yubicolabs.bank_app.services.storage.local.AccountStorage_Local;
import com.yubicolabs.bank_app.services.storage.local.AccountTransactionStorage_Local;

@SpringBootTest
class OpenApiGeneratorApplicationTests {

  @Test
  void AccountMethods_Local() {
    AccountStorage_Local local = new AccountStorage_Local();

    Account account1 = Account.builder().userHandle("my_user_handle").advancedProtection(false)
        .balance(3000).id(Long.valueOf(123456789)).build();

    assertTrue(local.create(account1));
    assertEquals(local.getAll("my_user_handle").size(), 1);
    System.out
        .println(local.getAll("my_user_handle").stream().findFirst().get()
            .getId() == 123456789);
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

  @Test
  void TransactionMethods_Local() {
    AccountTransactionStorage_Local local = new AccountTransactionStorage_Local();

    AccountTransaction newItem = AccountTransaction.builder()
        .type("deposit")
        .amount(2000)
        .description("to me")
        .status(true)
        .accountId(123456789).build();

    AccountTransaction newItem_2 = AccountTransaction.builder()
        .type("deposit")
        .amount(2000)
        .description("to me 2")
        .status(true)
        .accountId(123456789).build();

    AccountTransaction newItem_3 = AccountTransaction.builder()
        .type("deposit")
        .amount(2000)
        .description("to not me")
        .status(true)
        .accountId(987654321).build();

    assertTrue(local.create(newItem));
    assertEquals(local.getAll(123456789).size(), 1);

    local.create(newItem_2);
    assertEquals(local.getAll(123456789).size(), 2);

    local.create(newItem_3);
    assertEquals(local.getAll(987654321).size(), 1);
  }

}