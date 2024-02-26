package com.yubicolabs.bank_app.interfaces;

import java.time.Instant;
import java.util.Collection;

import com.yubicolabs.bank_app.models.common.AccountTransaction;

public interface AccountTransactionStorage {

  /**
   * Get all transactions associated to an account
   * 
   * @param accountId ID of the account
   * @return List of all transactions that belong to an account
   */
  public Collection<AccountTransaction> getAll(int accountId);

  /**
   * Create a new transaction for a specific account
   * 
   * @param accountTransaction Transaction to store
   * @return true if creation successful, false otherwise
   */
  public AccountTransaction create(String type, double amount, String description, Boolean status, int accountId,
      Instant createTime) throws Exception;
}
