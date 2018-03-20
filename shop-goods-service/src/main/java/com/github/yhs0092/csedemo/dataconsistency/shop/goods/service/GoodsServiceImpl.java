package com.github.yhs0092.csedemo.dataconsistency.shop.goods.service;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.apache.servicecomb.swagger.invocation.exception.InvocationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.yhs0092.csedemo.dataconsistency.shop.common.api.Goods;
import com.github.yhs0092.csedemo.dataconsistency.shop.common.api.GoodsService;
import com.github.yhs0092.csedemo.dataconsistency.shop.common.record.Recorder;
import com.github.yhs0092.csedemo.dataconsistency.shop.goods.dao.GoodsMapper;
import com.huawei.paas.cse.tcc.annotation.TccTransaction;

import io.swagger.annotations.ApiOperation;

@RestSchema(schemaId = "goodsService")
@RequestMapping(path = "/goods")
public class GoodsServiceImpl implements GoodsService {
  private static final Recorder RECORDER = new Recorder("goods");

  @Autowired
  private GoodsMapper goodsMapper;

//  @Autowired
//  private PlatformTransactionManager txManager;

  @Override
  @GetMapping(path = "/getById")
  public Goods getGoodsById(@RequestParam(name = "goodsId") Long goodsId) {
    if (null == goodsId) {
      throw new InvocationException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
          "goodsId should not be null");
    }
    return goodsMapper.getById(goodsId);
  }

  @Override
  @PostMapping(path = "/deductRemainingByTcc")
  @TccTransaction(cancelMethod = "cancelDeductRemaining", confirmMethod = "confirmDeductRemaining")
  public void deductRemainingByTcc(@RequestParam(name = "goodsId") Long goodsId,
      @RequestParam(name = "quantity") Integer quantity) {
    DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
//    TransactionStatus status = txManager.getTransaction(transactionDefinition);

    try {
      if (null == goodsId) {
        throw new InvocationException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "goodsId should not be null");
      }

      Goods goods = goodsMapper.getById(goodsId);
      if (null == goods) {
        throw new InvocationException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "goods not found, id = " + goodsId);
      }

      RECORDER.record("get goods: [{}], quantity = [{}]", goods, quantity);
      if (null == goods.getRemaining() || goods.getRemaining() < quantity) {
        RECORDER.record("remaining not enough");
        throw new InvocationException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "remaining goods is not enough, goods = [" + goods + "], quantity = [" + quantity + "]");
      }

      goods.setRemaining(goods.getRemaining() - quantity);
      RECORDER.record("update goods: [{}]", goods);
      goodsMapper.update(goods);
//      txManager.commit(status);
    } catch (Exception e) {
      RECORDER.record("catch an exception, {}", e);
//      txManager.rollback(status);
      throw e;
    }
  }

  @ApiOperation(hidden = true, value = "")
  public void cancelDeductRemaining(Long goodsId, Integer quantity) {
    RECORDER.record("deduct remaining cancelled! goodsId = [{}], quantity = [{}]", goodsId, quantity);
  }

  @ApiOperation(hidden = true, value = "")
  public void confirmDeductRemaining(Long goodsId, Integer quantity) {
    RECORDER.record("deduct remaining confirmed! goodsId = [{}], quantity = [{}]", goodsId, quantity);
  }

  @Override
  @PostMapping(path = "deductRemainingBy2PC")
  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
  public void deductRemainingBy2PC(@RequestParam(name = "goodsId") Long goodsId,
      @RequestParam(name = "quantity") Integer quantity) {
    try {
      Goods goods = goodsMapper.getById(goodsId);
      RECORDER.record("get goods: [{}], quantity = [{}]", goods, quantity);

      if (null == goods) {
        throw new InvocationException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "goods not found, id = " + goodsId);
      }
      if (null == goods.getRemaining() || goods.getRemaining() < quantity) {
        throw new InvocationException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "remaining goods is not enough, goods = [" + goods + "], quantity = [" + quantity + "]");
      }

      goods.setRemaining(goods.getRemaining() - quantity);
      RECORDER.record("update goods: [{}]", goods);
      goodsMapper.update(goods);
    } catch (Exception e) {
      RECORDER.record("catch an exception: {}", e);
      throw e;
    }
  }
}
