package com.baeldung.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;


public final class RemoteHystrixCommand extends HystrixCommand<String> {

  private final RemoteService remoteService;

  public RemoteHystrixCommand(
      Setter config, RemoteService simulator) {

    super(config);
    remoteService = simulator;
  }

  @Override
  protected String run() throws Exception {
    return remoteService.execute();
  }
}
