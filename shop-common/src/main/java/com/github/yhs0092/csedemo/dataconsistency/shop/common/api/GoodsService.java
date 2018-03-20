package com.github.yhs0092.csedemo.dataconsistency.shop.common.api;

public interface GoodsService {
  Goods getGoodsById(Long goodsId);

  void deductRemainingByTcc(Long goodsId, Integer quantity);

  void deductRemainingBy2PC(Long goodsId, Integer quantity);
}
