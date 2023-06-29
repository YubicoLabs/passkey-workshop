package com.yubicolabs.bank_app.services.bank;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.yubicolabs.bank_app.models.api.AccountDetailsResponse;
import com.yubicolabs.bank_app.models.api.AccountTransactionListResponse;
import com.yubicolabs.bank_app.models.api.AccountTransactionListResponseTransactionsInner;
import com.yubicolabs.bank_app.models.api.AdvancedProtectionStatusResponse;
import com.yubicolabs.bank_app.models.api.CreateAccountResponse;
import com.yubicolabs.bank_app.models.api.TransactionCreateResponse;
import com.yubicolabs.bank_app.models.api.UpdateAdvancedProtectionStatusResponse;
import com.yubicolabs.bank_app.models.common.Account;
import com.yubicolabs.bank_app.models.common.AccountTransaction;
import com.yubicolabs.bank_app.services.storage.StorageInstance;

import lombok.Getter;

@Service
@Scope("singleton")
public class BankOperations {

  @Getter
  @Autowired
  private StorageInstance storageInstance;

  public AccountDetailsResponse getAccountById(String accountId, String userhandle) throws Exception {
    Optional<Account> maybeAccount = storageInstance.getAccountStorage().get(Integer.parseInt(accountId));

    /**
     * Check that an account exists
     */
    if (!maybeAccount.isPresent()) {
      throw new Exception("The account does not exist");
    }

    Account account = maybeAccount.get();

    /**
     * Check that the requestor is the owner of the account
     * TODO, update the string passed into this method
     */
    if (!account.getUserHandle().equals(userhandle)) {
      throw new Exception("Your account is not authorized to access this resource");
    }

    return AccountDetailsResponse.builder()
        .accountId(account.getId().intValue())
        .balance(BigDecimal.valueOf(account.getBalance())).build();
  }

  public AccountTransactionListResponse getTransacationsByAccount(String accountId, String userhandle)
      throws Exception {

    Optional<Account> maybeAccount = storageInstance.getAccountStorage().get(Integer.parseInt(accountId));

    /**
     * Check that an account exists
     */
    if (!maybeAccount.isPresent()) {
      throw new Exception("The account does not exist");
    }

    Account account = maybeAccount.get();

    /**
     * Check that the requestor is the owner of the account
     * TODO, update the string passed into this method
     */
    if (!account.getUserHandle().equals(userhandle)) {
      throw new Exception("Your account is not authorized to access this resource");
    }

    Collection<AccountTransaction> transactions = storageInstance.getAccountTransactionStorage()
        .getAll(Integer.parseInt(accountId));

    List<AccountTransactionListResponseTransactionsInner> final_list = transactions.stream()
        .map(transaction -> AccountTransactionListResponseTransactionsInner.builder()
            .transactionId(transaction.getId().intValue())
            .type(transaction.getType())
            .amount(BigDecimal.valueOf(transaction.getAmount()))
            .transactionDate(transaction.getCreateTime().toString())
            .build())
        .collect(Collectors.toList());

    return AccountTransactionListResponse.builder().transactions(final_list).build();
  }

  public AdvancedProtectionStatusResponse getAdvancedProtectionStatus(String accountId, String userhandle)
      throws Exception {
    Optional<Account> maybeAccount = storageInstance.getAccountStorage().get(Integer.parseInt(accountId));

    /**
     * Check that an account exists
     */
    if (!maybeAccount.isPresent()) {
      throw new Exception("The account does not exist");
    }

    Account account = maybeAccount.get();

    /**
     * Check that the requestor is the owner of the account
     * TODO, update the string passed into this method
     */
    if (!account.getUserHandle().equals(userhandle)) {
      throw new Exception("Your account is not authorized to access this resource");
    }

    return AdvancedProtectionStatusResponse.builder().enabled(account.isAdvancedProtection()).build();

  }

  public CreateAccountResponse createAccount(String userhandle) throws Exception {
    /**
     * Consider adding a check where we check both the username and userhandle
     * against KeyCloak
     */

    Account new_account = Account.builder()
        .userHandle(userhandle)
        .advancedProtection(false) // default to false
        .balance(3000) // default to 3000
        .createTime(Instant.now())
        .build();

    boolean didCreate = storageInstance.getAccountStorage().create(new_account);

    if (didCreate) {
      return CreateAccountResponse.builder().status("created").build();
    } else {
      throw new Exception("There was an issue creating your account");
    }
  }

  public TransactionCreateResponse createTransaction(String type, double amount, String description, String userhandle)
      throws Exception {
    Optional<Account> maybeAccount = storageInstance.getAccountStorage().getAll(userhandle).stream().findFirst();

    /**
     * Check that an account exists
     */
    if (!maybeAccount.isPresent()) {
      throw new Exception("The account does not exist");
    }

    Account account = maybeAccount.get();

    /**
     * Check that the requestor is the owner of the account
     * TODO, update the string passed into this method
     */
    if (!account.getUserHandle().equals(userhandle)) {
      throw new Exception("Your account is not authorized to access this resource");
    }

    /**
     * TODO - Reconsider reimplementing this method
     * There's no reason to re-create the same object twice
     * Perhaps we pass in the different parts of a transaction to processTransaction
     * Then build the transaction object later for the DB
     * 
     * The primary problem is that we can't determine the status until we verify
     * that the transaction can be processed
     * 
     * Perhaps process transaction should return the accountransaction with a
     * relevant status
     */
    AccountTransaction new_transaction = AccountTransaction.builder()
        .type(type)
        .amount(amount)
        .description(description)
        .createTime(Instant.now())
        .accountId(account.getId().intValue())
        .build();

    boolean didCreate = storageInstance.getAccountStorage().processTransaction(new_transaction);

    AccountTransaction finalTransaction = AccountTransaction.builder()
        .type(new_transaction.getType())
        .amount(new_transaction.getAmount())
        .description(new_transaction.getDescription())
        .createTime(new_transaction.getCreateTime())
        .status(didCreate)
        .accountId(new_transaction.getAccountId())
        .build();

    boolean didCreate_trans = storageInstance.getAccountTransactionStorage().create(finalTransaction);
    if (didCreate_trans) {
      return TransactionCreateResponse.builder()
          .status(finalTransaction.getStatus() == true ? "complete" : "error")
          .build();
    } else {
      throw new Exception("[Error] there was an issue recording the transaction");
    }
  }

  public UpdateAdvancedProtectionStatusResponse updateAdvancedProtection(int accountId, boolean enabled,
      String userhandle)
      throws Exception {
    Optional<Account> maybeAccount = storageInstance.getAccountStorage().get(accountId);

    /**
     * Check that an account exists
     */
    if (!maybeAccount.isPresent()) {
      throw new Exception("The account does not exist");
    }

    Account account = maybeAccount.get();

    /**
     * Check that the requestor is the owner of the account
     * TODO, update the string passed into this method
     */
    if (!account.getUserHandle().equals(userhandle)) {
      throw new Exception("Your account is not authorized to access this resource");
    }

    boolean didUpdate = storageInstance.getAccountStorage().setAdvancedProtection(accountId, enabled);

    return UpdateAdvancedProtectionStatusResponse.builder()
        .status(didUpdate ? "complete" : "error").build();

  }

}
