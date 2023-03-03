package com.baeldung.hystrix;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HystrixController {

  @Autowired
  private HystrixControllerDelegate delegate;

  @RequestMapping("/hystrix")
  public String withHystrix() throws InterruptedException {
    return delegate.invokeRemoteServiceWithHystrix();
  }

  @RequestMapping("/any")
  public String any() throws InterruptedException {
    return delegate.invokeRemoteServiceWithOutHystrix();
  }
}
