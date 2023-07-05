package com.yubicolabs.bank_app.interfaces;

import java.util.Collection;
import java.util.Optional;
import java.time.Instant;

import com.yubicolabs.bank_app.models.common.Account;

public interface AccountStorage {

  /**
   * Creates a new user account
   * 
   * @param userhandle           ID of the user
   * @param isAdvancedProtection setting for advanced protection (default false)
   * @param balance              balance to enter into account
   * @param createTime           time of creation
   * 
   * @return new Account that was created
   * @throws Exception if account creation not successful
   */
  public Account create(String userhandle, boolean isAdvancedProtection, double balance, Instant createTime)
      throws Exception;

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
   * Process a withdraw or deposit for a specific account
   * 
   * @param accountId ID of the account to transact
   * @param type      deposit or withdraw
   * @param amount    amount to change in the account, based on type
   * @return true if processing successful, false otherwise
   */
  public boolean processTransaction(int accountId, String type, double amount);

}
