package com.github.yhs0092.csedemo.dataconsistency.shop.common.api;

public class Account {
  private Long id;

  /**
   * remaining balance
   */
  private Long balance;

  public Long getId() {
    return id;
  }

  public Account setId(Long id) {
    this.id = id;
    return this;
  }

  public Long getBalance() {
    return balance;
  }

  public Account setBalance(Long balance) {
    this.balance = balance;
    return this;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Account{");
    sb.append("id=").append(id);
    sb.append(", balance=").append(balance);
    sb.append('}');
    return sb.toString();
  }
}
