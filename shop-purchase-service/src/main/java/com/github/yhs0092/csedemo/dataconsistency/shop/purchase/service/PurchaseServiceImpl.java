package com.github.yhs0092.csedemo.dataconsistency.shop.purchase.service;

import org.apache.servicecomb.provider.pojo.RpcReference;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.apache.servicecomb.swagger.invocation.exception.InvocationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.yhs0092.csedemo.dataconsistency.shop.common.api.AccountService;
import com.github.yhs0092.csedemo.dataconsistency.shop.common.api.Goods;
import com.github.yhs0092.csedemo.dataconsistency.shop.common.api.GoodsService;
import com.github.yhs0092.csedemo.dataconsistency.shop.common.api.Purchase;
import com.github.yhs0092.csedemo.dataconsistency.shop.common.api.PurchaseService;
import com.github.yhs0092.csedemo.dataconsistency.shop.common.record.Recorder;
import com.github.yhs0092.csedemo.dataconsistency.shop.purchase.dao.PurchaseMapper;
import com.huawei.paas.cse.tcc.annotation.TccTransaction;

import io.swagger.annotations.ApiOperation;

@RestSchema(schemaId = "purchaseService")
@RequestMapping(path = "/purchase")
public class PurchaseServiceImpl implements PurchaseService {
  private static final Recorder RECORDER = new Recorder("purchase");

  @Autowired
  private PurchaseMapper purchaseMapper;

//  @Autowired
//  private PlatformTransactionManager txManager;

  @RpcReference(microserviceName = "account-service", schemaId = "accountService")
  private AccountService accountService;

  @RpcReference(microserviceName = "goods-service", schemaId = "goodsService")
  private GoodsService goodsService;

  @Override
  @GetMapping(path = "/getById")
  public Purchase getPurchaseById(@RequestParam(value = "purchaseId") Long purchaseId) {
    if (null == purchaseId) {
      throw new InvocationException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
          "purchaseId should not be null");
    }
    return purchaseMapper.getById(purchaseId);
  }

  @Override
  @PostMapping(path = "/doPurchaseByTcc")
  @TccTransaction(cancelMethod = "cancelPurchase", confirmMethod = "confirmPurchase")
  public void doPurchaseByTcc(@RequestBody Purchase purchase) {
    checkPurchaseParam(purchase);

    DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
//    TransactionStatus status = txManager.getTransaction(transactionDefinition);

    try {
      initPurchase(purchase, Purchase.PURCHASE_TYPE_TCC);

      RECORDER.record("create new purchase record: [{}]", purchase);
      purchaseMapper.insert(purchase);

      RECORDER.record("deduct account balance");
      accountService.deductBalanceByTcc(purchase.getAccountId(), purchase.getTotalPrice());

      RECORDER.record("deduct remaining goods");
      goodsService.deductRemainingByTcc(purchase.getGoodsId(), purchase.getQuantity());

//      txManager.commit(status);
    } catch (Exception e) {
      RECORDER.record("catch an exception, {}", e);
//      txManager.rollback(status);
      throw e;
    }
  }

  @ApiOperation(hidden = true, value = "")
  public void cancelPurchase(Purchase purchase) {
    purchase.setStatus(Purchase.STATUS_CANCELLED);
    RECORDER.record("cancel purchase: [{}]", purchase);
    purchaseMapper.update(purchase);
  }

  @ApiOperation(hidden = true, value = "")
  public void confirmPurchase(Purchase purchase) {
    purchase.setStatus(Purchase.STATUS_CONFIRMED);
    RECORDER.record("confirm purchase: [{}]", purchase);
    purchaseMapper.update(purchase);
  }

  @Override
  @PostMapping(path = "doPurchaseBy2PC")
  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
  public void doPurchaseBy2PC(@RequestBody Purchase purchase) {
    checkPurchaseParam(purchase);

    try {
      initPurchase(purchase, Purchase.PURCHASE_TYPE_2PC);

      RECORDER.record("create new purchase record: [{}]", purchase);
      purchaseMapper.insert(purchase);

      RECORDER.record("deduct account balance");
      accountService.deductBalanceByTcc(purchase.getAccountId(), purchase.getTotalPrice());

      RECORDER.record("deduct remaining goods");
      goodsService.deductRemainingByTcc(purchase.getGoodsId(), purchase.getQuantity());

      purchase.setStatus(Purchase.STATUS_CONFIRMED);
      RECORDER.record("purchase completed, purchase: [{}]", purchase);
      purchaseMapper.update(purchase);
    } catch (Exception e) {
      RECORDER.record("catch an exception: {}", e);
      purchase.setStatus(Purchase.STATUS_CANCELLED);
      purchaseMapper.update(purchase);
      throw e;
    }
  }

  private void initPurchase(@RequestBody Purchase purchase, String purchaseType) {
    RECORDER.record("do purchase: [{}]", purchase);
    Goods goods = goodsService.getGoodsById(purchase.getGoodsId());
    purchase.setPurchaseType(purchaseType)
        .setTotalPrice(goods.getPrice() * purchase.getQuantity())
        .setStatus(Purchase.STATUS_UNKNOWN);
  }

  private void checkPurchaseParam(@RequestBody Purchase purchase) {
    String nullField = null;
    if (null == purchase) {
      nullField = "purchase";
    } else if (null == purchase.getAccountId()) {
      nullField = "accountId";
    } else if (null == purchase.getGoodsId()) {
      nullField = "goodsId";
    } else if (null == purchase.getQuantity()) {
      nullField = "quantity";
    }
    if (null != nullField) {
      throw new InvocationException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
          nullField + " should not be null");
    }
  }
}
