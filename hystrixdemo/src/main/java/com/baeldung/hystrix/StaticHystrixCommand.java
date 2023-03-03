package com.baeldung.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;


public class StaticHystrixCommand extends HystrixCommand<String> {

  private final String message;

  public StaticHystrixCommand(String value) {

    super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));

    message = value;
  }

  @Override
  protected String run() {
    return "Incoming message " + message;
  }
}
