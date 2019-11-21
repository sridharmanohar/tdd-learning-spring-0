package com.tdd.spring;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetroController {

  private final MetroService metroService;

  @Autowired
  public MetroController(MetroService metroService) {
    this.metroService = metroService;
  }


  /*
   * public MetroController(MetroService metroService) {
   * System.out.println("in controller const."); this.metroService = metroService;
   * }
   */
  @GetMapping("/metro")
  public Set<String> getMetros() {
    System.out.println("/metro has been called");
    return metroService.getMetros();
  }

}
