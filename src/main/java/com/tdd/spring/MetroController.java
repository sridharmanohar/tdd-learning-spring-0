package com.tdd.spring;

import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetroController {
  
  private MetroService metroService;
  
  public MetroController(MetroService metroService) {
    this.metroService = metroService;
  }

  @GetMapping("/metro")
  public Set<String> getMetros() {
    return metroService.getMetros();
  }

}
