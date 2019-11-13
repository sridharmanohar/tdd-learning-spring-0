package com.tdd.spring;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class MyService {

  private static Map<String, String> metros = new HashMap<>();
  
  static {
    metros.put("Bengaluru", "Confirmed");
    metros.put("Chennai", "Confirmed");
    metros.put("Kolkata", "Confirmed");
    metros.put("Mumbai", "Confirmed");
    metros.put("Delhi", "Confirmed");
    metros.put("Hyderabad", "Confirmed");
  }
  
  public String greet() {
    return null;
  }

  public Map<String, String> getMetros() {
    return metros;
  }

  public String addMetro(String newMetro) {
    if(metros.containsKey(newMetro))
      throw new IllegalArgumentException();
    metros.put(newMetro, "Proposed");
    return "New metro proposal submitted.";
  }
 
}
