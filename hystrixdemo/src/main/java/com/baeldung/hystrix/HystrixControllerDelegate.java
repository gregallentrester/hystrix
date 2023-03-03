package com.baeldung.hystrix;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component("delegate")
public class HystrixControllerDelegate {

  @Value("${remoteservice.timeout}")
  private int latency;

  @HystrixCircuitBreaker
  public final String invokeRemoteServiceWithHystrix() throws InterruptedException{
    return new RemoteService(latency).execute();
  }

  public final String invokeRemoteServiceWithOutHystrix() throws InterruptedException{
    return new RemoteService(latency).execute();
  }
}
