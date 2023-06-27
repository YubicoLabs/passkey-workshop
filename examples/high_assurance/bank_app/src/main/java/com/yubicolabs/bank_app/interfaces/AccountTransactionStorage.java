package com.yubicolabs.bank_app.interfaces;

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
   * @param accountId   ID of the account
   * @param type        determine if this is a deposit or withdrawal
   * @param amount      amount of money to add/remove from account (based on type)
   * @param description note attached to transaction
   * @param status      determine if transaction was successful or not
   * @return
   */
  public AccountTransaction create(int accountId, String type, double amount, String description, boolean status);
}
