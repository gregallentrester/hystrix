package com.baeldung.hystrix;


public final class RemoteService {

  private long wait;

  RemoteService(long value) throws InterruptedException {
    wait = value;
  }

  public String execute() throws InterruptedException {

    System.err.println(
      "\n\nRemoteService.execute() waits: " + wait + "\n");

    Thread.sleep(wait);

    return "Success";
  }
}
