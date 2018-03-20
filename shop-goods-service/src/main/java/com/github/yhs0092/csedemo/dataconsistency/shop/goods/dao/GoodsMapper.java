package com.github.yhs0092.csedemo.dataconsistency.shop.goods.dao;

import com.github.yhs0092.csedemo.dataconsistency.shop.common.api.Goods;

public interface GoodsMapper {
  Goods getById(Long id);

  void update(Goods goods);
}
