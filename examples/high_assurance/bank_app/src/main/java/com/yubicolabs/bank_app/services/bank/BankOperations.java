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
import com.yubicolabs.bank_app.models.api.CreateAccountRequest;
import com.yubicolabs.bank_app.models.api.CreateAccountResponse;
import com.yubicolabs.bank_app.models.api.TransactionCreateRequest;
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

  /**
   * Assume the user handle from the user's auth token
   * Used to perform checks in the application to ensure that a user should have
   * access to account information
   * 
   * @param tempObject TODO
   *                   returns the user handle presented in the user's auth token
   */
  private String getUserHandle(String tempObject) {
    /**
     * This method will be used to assume the user handle from the auth token
     * TODO - Implement once we have a sense of the auth flow to this API + Spring
     */
    return "TODO";
  }

  public AccountDetailsResponse getAccountById(String accountId) throws Exception {
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
    if (!account.getUserHandle().equals(getUserHandle(accountId))) {
      throw new Exception("Your account is not authorized to access this resource");
    }

    return AccountDetailsResponse.builder()
        .accountId(account.getId().intValue())
        .balance(BigDecimal.valueOf(account.getBalance())).build();
  }

  public AccountTransactionListResponse getTransacationsByAccount(String accountId) throws Exception {

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
    if (!account.getUserHandle().equals(getUserHandle(accountId))) {
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

  public AdvancedProtectionStatusResponse getAdvancedProtectionStatus(String accountId) throws Exception {
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
    if (!account.getUserHandle().equals(getUserHandle(accountId))) {
      throw new Exception("Your account is not authorized to access this resource");
    }

    return AdvancedProtectionStatusResponse.builder().enabled(account.isAdvancedProtection()).build();

  }

  public CreateAccountResponse createAccount(CreateAccountRequest accountRequest) throws Exception {
    /**
     * Consider adding a check where we check both the username and userhandle
     * against KeyCloak
     */

    Account new_account = Account.builder()
        .userHandle(getUserHandle("TODO pass uername and get UID"))
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

  public TransactionCreateResponse createTransaction(TransactionCreateRequest request) throws Exception {
    String userhandle = getUserHandle("TODO get userhanlde");

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

    AccountTransaction new_transaction = AccountTransaction.builder()
        .type(request.getType())
        .amount(request.getAmount().doubleValue())
        .description(request.getDescription())
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

  public UpdateAdvancedProtectionStatusResponse updateAdvancedProtection(int accountId, boolean enabled)
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
    if (!account.getUserHandle().equals(getUserHandle("TODO-Add UH"))) {
      throw new Exception("Your account is not authorized to access this resource");
    }

    boolean didUpdate = storageInstance.getAccountStorage().setAdvancedProtection(accountId, enabled);

    return UpdateAdvancedProtectionStatusResponse.builder()
        .status(didUpdate ? "complete" : "error").build();

  }

}
