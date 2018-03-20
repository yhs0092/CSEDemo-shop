package com.github.yhs0092.csedemo.dataconsistency.shop.common.record;

import org.apache.servicecomb.core.Const;
import org.apache.servicecomb.swagger.invocation.context.ContextUtils;
import org.apache.servicecomb.swagger.invocation.context.InvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Recorder {
  private static final Logger LOGGER = LoggerFactory.getLogger("recordlog");

  private String recorderName;

  public Recorder(String recorderName) {
    this.recorderName = recorderName;
  }

  public void record(String message) {
    InvocationContext invocationContext = ContextUtils.getInvocationContext();
    String traceId = invocationContext.getContext(Const.TRACE_ID_NAME);

    LOGGER.info("[" + getInvokerMethod() + "] - [" + recorderName + "] - [" + traceId + "] - " + message);
  }

  public void record(String pattern, Object... objects) {
    InvocationContext invocationContext = ContextUtils.getInvocationContext();
    String traceId = invocationContext.getContext(Const.TRACE_ID_NAME);

    LOGGER.info("[" + getInvokerMethod() + "] - [" + recorderName + "] - [" + traceId + "] - " + pattern, objects);
  }

  private String getInvokerMethod() {
    StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
    if (stackTraceElements.length < 4) {
      return "-";
    }
    StackTraceElement invoker = stackTraceElements[3];
    return invoker.getClassName() + "." + invoker.getMethodName();
  }
}
