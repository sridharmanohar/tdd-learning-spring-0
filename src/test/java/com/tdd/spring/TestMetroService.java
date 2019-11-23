package com.tdd.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class TestMetroService {

  private final MetroService metroService;

  @MockBean
  private MetroRepository metroRepository;

  @Autowired
  public TestMetroService(MetroService metroService) {
    this.metroService = metroService;
  }

  /**
   * verify service layer returns a list of metros when it calls the necessary
   * repository method.
   */
  @Test
  public void verify_metros_result_set_noduplicates() {
    List<Metro> metros = new ArrayList<>();
    Metro metro0 = new Metro("Hyderabad");
    Metro metro1 = new Metro("Chennai");
    Metro metro2 = new Metro("Bengaluru");
    Metro metro3 = new Metro("Kolkata");
    Metro metro4 = new Metro("Mumbai");
    Metro metro5 = new Metro("Hyderabad");
    Metro metro6 = new Metro("Chennai");
    metros.add(metro0);
    metros.add(metro1);
    metros.add(metro2);
    metros.add(metro3);
    metros.add(metro4);
    metros.add(metro5);
    metros.add(metro6);
    Mockito.when(metroRepository.findAll()).thenReturn(metros);
    Assertions.assertTrue(() -> {
      Set<String> tempSet = metroService.getMetros();
      if (tempSet != null && !tempSet.isEmpty() && tempSet.size() == 5)
        return true;
      else
        return false;
    });

  }

  
  @ParameterizedTest
  @ValueSource(strings = {"Delhi", "Pune"})
  public void whenGivenNewMetroProposal__thenReturnSuccessMessage(String proposedMetro) {
    Mockito.when(this.metroRepository.findByName(proposedMetro)).thenReturn(null);
    assertEquals("OK", this.metroService.performMetroSubmission(proposedMetro));
  }

  @ParameterizedTest
  @ValueSource(strings = {"Hyderabad", "Bengaluru"})
  public void whenGivenExistingMetroName__thenReturnInvalidMessage(String proposedMetro) {
    Metro metro = new Metro();
    metro.setStatus("confirmed");
    Mockito.when(this.metroRepository.findByName(proposedMetro)).thenReturn(metro);
    assertEquals("confirmed", this.metroService.performMetroSubmission(proposedMetro));
  }

  @ParameterizedTest
  @ValueSource(strings = {"Chandigarh"})
  public void whenGivenAlreadyProposedMetroName__thenReturnInvalidMessage(String proposedMetro) {
    Metro metro = new Metro();
    metro.setStatus("proposed");
    Mockito.when(this.metroRepository.findByName(proposedMetro)).thenReturn(metro);
    assertEquals("proposed", this.metroService.performMetroSubmission(proposedMetro));
  }

  
}
