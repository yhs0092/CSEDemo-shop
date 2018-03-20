package com.github.yhs0092.csedemo.dataconsistency.shop.common.api;

public interface AccountService {
  Account getAccountById(Long accountId);

  /**
   * Search an account by id, and deduct the cost from {@link Account#balance}
   * @param accountId id of {@link Account}
   * @param cost the cost deducted from {@link Account#balance}
   */
  void deductBalanceByTcc(Long accountId, Long cost);

  /**
   * @see #deductBalanceByTcc(Long, Long)
   */
  void deductBalanceBy2PC(Long accountId, Long cost);
}
