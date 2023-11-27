package com.yubicolabs.bank_app.services.bank;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.security.sasl.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.yubicolabs.bank_app.models.api.AccountDetailListResponse;
import com.yubicolabs.bank_app.models.api.AccountResponse;
import com.yubicolabs.bank_app.models.api.AccountTransactionListResponse;
import com.yubicolabs.bank_app.models.api.AccountTransactionResponse;
import com.yubicolabs.bank_app.models.api.CreateAccountResponse;
import com.yubicolabs.bank_app.models.api.TransactionCreateResponse;
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
   * Get all accounts for a given user handle
   * 
   * @param userhandle ID of the user
   * @returns A list of all the accounts owned by the user
   * @throws Exception
   */
  public AccountDetailListResponse getAccountsByUserhandle(String userhandle) throws Exception {
    Collection<Account> accountList = storageInstance.getAccountStorage().getAll(userhandle);

    List<AccountResponse> responseList = accountList.stream().map(account -> AccountResponse.builder()
        .accountId(account.getId().intValue())
        .balance(BigDecimal.valueOf(account.getBalance()))
        .build()).collect(Collectors.toList());

    return AccountDetailListResponse.builder().accounts(responseList).build();
  }

  public AccountResponse getAccountById(int accountId, String userhandle) throws Exception {
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

    return AccountResponse.builder()
        .accountId(account.getId().intValue())
        .balance(BigDecimal.valueOf(account.getBalance()))
        .build();
  }

  public AccountTransactionListResponse getTransactionsByAccount(int accountId, String userhandle)
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

    Collection<AccountTransaction> transactions = storageInstance.getAccountTransactionStorage()
        .getAll(accountId);

    List<AccountTransactionResponse> final_list = transactions.stream()
        .map(transaction -> AccountTransactionResponse.builder()
            .transactionId(transaction.getId().intValue())
            .type(AccountTransactionResponse.TypeEnum.fromValue(transaction.getType()))
            .amount(BigDecimal.valueOf(transaction.getAmount()))
            .transactionDate(transaction.getCreateTime().toString())
            .description(transaction.getDescription())
            .build())
        .collect(Collectors.toList());

    return AccountTransactionListResponse.builder().transactions(final_list).build();
  }

  public CreateAccountResponse createAccount(String userhandle) throws Exception {
    /**
     * Consider adding a check where we check both the username and userhandle
     * against KeyCloak
     */

    try {
      if (storageInstance.getAccountStorage().getAll(userhandle).size() > 0) {
        throw new Exception("This user already has an account");
      }

      storageInstance.getAccountStorage().create(userhandle, 3000, Instant.now());

      return CreateAccountResponse.builder().status("created").build();
    } catch (Exception e) {
      throw e;
    }

  }

  public TransactionCreateResponse createTransaction(int acr, String type, double amount, String description,
      String userhandle)
      throws Exception {
    try {
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
       * Check that the requestor has the right ACR (Level of Assurance) to process
       * the transaction
       * ACR of 1 is needed for all transactions < 1000
       * ACR of 2 is needed for all transactions >= 1000
       */

      if (amount >= 1000 && acr < 2) {
        throw new AuthenticationException("User does not have the correct permissions. Please reauthenticate");
      }

      boolean didCreate = storageInstance.getAccountStorage().processTransaction(account.getId().intValue(), type,
          amount);

      AccountTransaction finalTransaction = storageInstance.getAccountTransactionStorage().create(type, amount,
          description, didCreate, account.getId().intValue(), Instant.now());

      return TransactionCreateResponse.builder()
          .status(finalTransaction.getStatus() == true ? "complete" : "error")
          .transactionId(finalTransaction.getId().intValue())
          .build();
    } catch (AuthenticationException e) {
      throw e;
    } catch (Exception e) {
      throw new Exception("[Error] there was an issue recording the transaction");
    }
  }
}
