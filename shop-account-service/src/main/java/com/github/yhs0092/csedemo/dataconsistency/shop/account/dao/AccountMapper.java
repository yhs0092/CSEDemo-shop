package com.github.yhs0092.csedemo.dataconsistency.shop.account.dao;

import com.github.yhs0092.csedemo.dataconsistency.shop.common.api.Account;

public interface AccountMapper {
  Account getById(Long id);

  /**
   * Search account by {@link Account#id}, and update it's property.
   */
  void update(Account account);
}
