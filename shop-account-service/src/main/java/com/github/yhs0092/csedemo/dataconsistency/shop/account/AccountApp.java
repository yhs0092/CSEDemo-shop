package com.github.yhs0092.csedemo.dataconsistency.shop.account;


import org.apache.servicecomb.foundation.common.utils.BeanUtils;
import org.apache.servicecomb.foundation.common.utils.Log4jUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountApp {
  private static final Logger LOGGER= LoggerFactory.getLogger(AccountApp.class);
  public static void main(String[] args) throws Exception {
    Log4jUtils.init();
    BeanUtils.init();
  }
}
