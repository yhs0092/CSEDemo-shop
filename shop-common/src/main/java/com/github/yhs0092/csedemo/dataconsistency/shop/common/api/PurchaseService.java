package com.github.yhs0092.csedemo.dataconsistency.shop.common.api;

public interface PurchaseService {
  Purchase getPurchaseById(Long purchaseId);

  void doPurchaseByTcc(Purchase purchase);

  void doPurchaseBy2PC(Purchase purchase);
}
