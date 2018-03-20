package com.github.yhs0092.csedemo.dataconsistency.shop.purchase.dao;

import com.github.yhs0092.csedemo.dataconsistency.shop.common.api.Purchase;

public interface PurchaseMapper {
  Purchase getById(Long id);

  void update(Purchase purchase);

  void insert(Purchase purchase);
}
