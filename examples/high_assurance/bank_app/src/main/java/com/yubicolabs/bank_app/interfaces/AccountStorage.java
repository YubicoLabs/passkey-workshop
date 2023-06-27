package com.yubicolabs.bank_app.interfaces;

import java.util.Collection;
import java.util.Optional;

import com.yubicolabs.bank_app.models.common.Account;
import com.yubicolabs.bank_app.models.common.AccountTransaction;

public interface AccountStorage {

  /**
   * Create a new account for a user
   * 
   * @param userHandle ID of the user attempting to create the account
   * @return true if account created, false otherwise
   */
  public boolean create(Account account);

  /**
   * Get all accounts for a specific user
   * 
   * @param userHandle User ID for account owner
   * @return List of all accounts that belong to a user
   */
  public Collection<Account> getAll(String userHandle);

  /**
   * Get a specific account
   * 
   * @param accountId ID of the account to retrieve from repository
   * @return Account object
   */
  public Optional<Account> get(int accountId);

  /**
   * Update the setting of an accounts advanced protection setting
   * 
   * @param accountId The ID of the account being updated
   * @param setting   The desired AP setting for the account
   *                  Note - It is possible to set an account with true to true,
   *                  this will still result in a successful request (same with
   *                  false to false)
   * @return true if the account was updated, false otherwise
   */
  public boolean setAdvancedProtection(int accountId, boolean setting);

  /**
   * Process a transaction to update the account balance
   * 
   * @param transaction transaction to be processed for the account
   * @return true if processing successful, false otherwise
   */
  public boolean processTransaction(AccountTransaction transaction);

}
