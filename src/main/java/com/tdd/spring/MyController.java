package com.tdd.spring;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MyController {

  private MyService myService;

  public MyController(MyService myService) {
    this.myService = myService;
  }

  @GetMapping("/")
  public @ResponseBody String showWelcomeMessage() {
    return myService.greet();
  }

  @GetMapping("/login")
  public String showLoginView() {
    return "login.html";
  }

  @GetMapping("/metros")
  public @ResponseBody Map<String,String> showMetros() {
    return myService.getMetros();
  }
  
}
