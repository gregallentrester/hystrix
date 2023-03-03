package com.baeldung.hystrix;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.exception.HystrixRuntimeException;

import org.junit.Test;


public class HystrixTimeoutManualTest {

  private String ID = "";

  @Test
  public void givenInputBobAndDefaultSettings_whenCommandExecuted_thenReturnHelloBob() {
    assertThat(new StaticHystrixCommand(ID).execute(), equalTo("Incoming message " + ID));
  }

  @Test
  public void givenSvcTimeoutOf100AndDefaultSettings_whenRemoteSvcExecuted_thenReturnSuccess()
      throws InterruptedException {

    HystrixCommand.Setter config =
      HystrixCommand.Setter.
        withGroupKey(HystrixCommandGroupKey.Factory.asKey("RemoteServiceGroup2"));

    assertThat(
      new RemoteHystrixCommand(config, new RemoteService(100)).execute(),
      equalTo("Success"));
  }

  @Test(expected = HystrixRuntimeException.class)
  public void givenSvcTimeoutOf10000AndDefaultSettings__whenRemoteSvcExecuted_thenExpectHRE()
      throws InterruptedException {

    HystrixCommand.Setter config =
      HystrixCommand.Setter.
        withGroupKey(HystrixCommandGroupKey.Factory.asKey("RemoteServiceGroupTest3"));

    new RemoteHystrixCommand(config, new RemoteService(10_000)).execute();
  }

  @Test
  public void givenSvcTimeoutOf5000AndExecTimeoutOf10000_whenRemoteSvcExecuted_thenReturnSuccess()
      throws InterruptedException {

    HystrixCommand.Setter config =
      HystrixCommand.Setter.
        withGroupKey(HystrixCommandGroupKey.Factory.asKey("RemoteServiceGroupTest4"));

    HystrixCommandProperties.Setter commandProperties =
      HystrixCommandProperties.Setter();

    commandProperties.withExecutionTimeoutInMilliseconds(10_000);

    config.andCommandPropertiesDefaults(commandProperties);

    assertThat(
      new RemoteHystrixCommand(config, new RemoteService(500)).execute(),
      equalTo("Success"));
  }

  @Test(expected = HystrixRuntimeException.class)
  public void givenSvcTimeoutOf15000AndExecTimeoutOf5000__whenExecuted_thenExpectHRE()
      throws InterruptedException {

    HystrixCommand.Setter config =
      HystrixCommand.Setter.
      withGroupKey(HystrixCommandGroupKey.Factory.asKey("RemoteServiceGroupTest5"));

    HystrixCommandProperties.Setter commandProperties =
      HystrixCommandProperties.Setter();

    commandProperties.withExecutionTimeoutInMilliseconds(5_000);

    config.andCommandPropertiesDefaults(commandProperties);

    new RemoteHystrixCommand(
      config, new RemoteService(15_000)).
        execute();
 }

  @Test
  public void givenSvcTimeoutOf500AndExecTimeoutOf10000AndThreadPool__whenExecuted_thenReturnSuccess()
      throws InterruptedException {

    HystrixCommand.Setter config =
      HystrixCommand.Setter.
        withGroupKey(HystrixCommandGroupKey.Factory.asKey("RemoteServiceGroupThreadPool"));

    HystrixCommandProperties.Setter commandProperties =
      HystrixCommandProperties.Setter();

    commandProperties.withExecutionTimeoutInMilliseconds(10_000);

    config.andCommandPropertiesDefaults(commandProperties);

    config.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().
      withMaxQueueSize(10).
      withCoreSize(3).
      withQueueSizeRejectionThreshold(10));

    assertThat(
      new RemoteHystrixCommand(config, new RemoteService(500)).execute(),
      equalTo("Success"));
  }

  @Test
  public void givenCircuitBreakerSetup__whenRemoteSvcCmdExecuted_thenReturnSuccess()
      throws InterruptedException {

    HystrixCommand.Setter config =
      HystrixCommand.Setter.
       withGroupKey(HystrixCommandGroupKey.Factory.asKey("RemoteServiceGroupCircuitBreaker"));

    HystrixCommandProperties.Setter properties =
      HystrixCommandProperties.Setter();
    properties.withExecutionTimeoutInMilliseconds(1000);

    properties.withCircuitBreakerSleepWindowInMilliseconds(4000);

    properties.withExecutionIsolationStrategy(
      HystrixCommandProperties.ExecutionIsolationStrategy.THREAD);

    properties.withCircuitBreakerEnabled(true);
    properties.withCircuitBreakerRequestVolumeThreshold(1);

    config.andCommandPropertiesDefaults(properties);

    config.andThreadPoolPropertiesDefaults(
      HystrixThreadPoolProperties.Setter().
      withMaxQueueSize(1).
      withCoreSize(1).
      withQueueSizeRejectionThreshold(1));

    assertThat(this.invokeRemoteService(config, 10_000), equalTo(null));
    assertThat(this.invokeRemoteService(config, 10_000), equalTo(null));
    assertThat(this.invokeRemoteService(config, 10_000), equalTo(null));

    Thread.sleep(5000);

    assertThat(
      new RemoteHystrixCommand(config, new RemoteService(500)).execute(),
      equalTo("Success"));

    assertThat(
      new RemoteHystrixCommand(config, new RemoteService(500)).execute(),
      equalTo("Success"));

    assertThat(
      new RemoteHystrixCommand(config, new RemoteService(500)).execute(),
      equalTo("Success"));
  }

  public String invokeRemoteService(HystrixCommand.Setter config, int timeout)
      throws InterruptedException {

    String response = null;

    try {

      response =
        new RemoteHystrixCommand(
          config,
          new RemoteService(timeout)).
            execute();
    }
    catch (HystrixRuntimeException e) {
      System.out.println("ex = " + e);
    }

    return response;
  }
}
