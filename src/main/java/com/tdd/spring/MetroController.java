package com.tdd.spring;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetroController {

  private final MetroService metroService;

  @Autowired
  public MetroController(MetroService metroService) {
    this.metroService = metroService;
  }

  @GetMapping("/metro")
  public Set<String> getMetros() {
    System.out.println("/metro has been called");
    return metroService.getMetros();
  }

  @PostMapping("/proposeMetro/{metroName}")
  public ResponseEntity<Set<Metro>> proposeMetro(@PathVariable(name = "metroName") String proposedMetro) {
    Set<Metro> metros = this.metroService.performMetroSubmission(proposedMetro);
    String res = "";
    if(metros.size() == 1) {
      for(Metro m : metros) {
        res = m.getStatus();
      }
    }
    if (metros.size() == 1 && res.equalsIgnoreCase("proposed")) {
      return new ResponseEntity<Set<Metro>>(metros, HttpStatus.BAD_REQUEST);
    }
    else if (metros.size() == 1 && res.equalsIgnoreCase("confirmed")) {
      return new ResponseEntity<Set<Metro>>(metros, HttpStatus.BAD_REQUEST);
    }
    else if (metros.size() > 1) {
      System.out.println("metro size : "+ metros.size());
      return new ResponseEntity<>(metros, HttpStatus.OK);
    }
   return null;
  }

}
