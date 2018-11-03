package com.dinstone.loghub.spi;

public interface LogDelegateFactory {
    LogDelegate createDelegate(String name);
  }
