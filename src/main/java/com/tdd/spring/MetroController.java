package com.tdd.spring;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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
  public String proposeMetro(@PathVariable(name = "metroName") String proposedMetro) {
    String status = "";
    Set<Metro> metros = this.metroService.performMetroSubmission(proposedMetro);
    String res = "";
    for(Metro m : metros) {
      res = m.getStatus();
    }
        
    if (metros.size() == 1 && res.equalsIgnoreCase("proposed"))
      status = proposedMetro + " has been proposed already!!";
    else if (metros.size() == 1 && res.equalsIgnoreCase("confirmed"))
      status = proposedMetro + " is already a Metro, nothing to propose!";
    else if (metros.size() > 1)
      status = proposedMetro + " is successfully submitted for proposal.";
    return status;
  }

}
