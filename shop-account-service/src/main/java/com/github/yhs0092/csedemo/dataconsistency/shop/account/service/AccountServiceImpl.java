package com.github.yhs0092.csedemo.dataconsistency.shop.account.service;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.apache.servicecomb.swagger.invocation.exception.InvocationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.yhs0092.csedemo.dataconsistency.shop.account.dao.AccountMapper;
import com.github.yhs0092.csedemo.dataconsistency.shop.common.api.Account;
import com.github.yhs0092.csedemo.dataconsistency.shop.common.api.AccountService;
import com.github.yhs0092.csedemo.dataconsistency.shop.common.record.Recorder;
import com.huawei.paas.cse.tcc.annotation.TccTransaction;

import io.swagger.annotations.ApiOperation;

@RestSchema(schemaId = "accountService")
@RequestMapping(path = "/account")
public class AccountServiceImpl implements AccountService {
  private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);

  private static final Recorder RECORDER = new Recorder("account");

  @Autowired
  private AccountMapper accountMapper;

//  @Autowired
//  private PlatformTransactionManager txManager;

  @Override
  @GetMapping(path = "/getById")
  public Account getAccountById(@RequestParam(name = "accountId") Long accountId) {
    if (null == accountId) {
      LOGGER.error("get a null id!");
      throw new InvocationException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
          "accountId should not be null");
    }
    return accountMapper.getById(accountId);
  }

  @Override
  @PostMapping(path = "/deductBalanceByTcc")
  @TccTransaction(cancelMethod = "cancelDeductBalance", confirmMethod = "confirmDeductBalance")
  public void deductBalanceByTcc(@RequestParam(name = "accountId") Long accountId,
      @RequestParam(name = "cost") Long cost) {
    DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
//    TransactionStatus status = txManager.getTransaction(transactionDefinition);

    try {
      if (null == accountId) {
        throw new InvocationException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "accountId should not be null");
      }

      Account account = accountMapper.getById(accountId);
      if (null == account) {
        throw new InvocationException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "account not found, id = " + accountId);
      }

      RECORDER.record("get account: [{}], cost = [{}]", account, cost);
      if (null == account.getBalance() || account.getBalance() < cost) {
        RECORDER.record("balance not enough");
        throw new InvocationException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "account balance is not enough, account = [" + account + "], cost = [" + cost + "]");
      }

      account.setBalance(account.getBalance() - cost);
      RECORDER.record("update account: [{}]", account);
      accountMapper.update(account);
//      txManager.commit(status);
    } catch (Exception e) {
      RECORDER.record("catch an exception: {}", e);
//      txManager.rollback(status);
      throw e;
    }
  }

  @ApiOperation(hidden = true, value = "")
  public void cancelDeductBalance(Long accountId, Long cost) {
    RECORDER.record("deduct balance cancelled! accountId = [{}], cost = [{}]", accountId, cost);
  }

  @ApiOperation(hidden = true, value = "")
  public void confirmDeductBalance(Long accountId, Long cost) {
    RECORDER.record("deduct balance confirmed! accountId = [{}], cost = [{}]", accountId, cost);
  }

  @Override
  @PostMapping(path = "deductBalanceBy2PC")
  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
  public void deductBalanceBy2PC(@RequestParam(name = "accountId") Long accountId,
      @RequestParam(name = "cost") Long cost) {
    try {
      Account account = accountMapper.getById(accountId);
      RECORDER.record("get account: [{}], cost = [{}]", account, cost);

      if (null == account) {
        throw new InvocationException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "account not found, id = " + accountId);
      }
      if (null == account.getBalance() || account.getBalance() < cost) {
        throw new InvocationException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "account balance is not enough, account = [" + account + "], cost = [" + cost + "]");
      }

      account.setBalance(account.getBalance() - cost);
      RECORDER.record("update account: [{}]", account);
      accountMapper.update(account);
    } catch (Exception e) {
      RECORDER.record("catch an exception: {}", e);
      throw e;
    }
  }
}
