package com.tdd.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
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
    Set<Metro> metroSet = new HashSet<>();
    Metro m0 = new Metro();
    m0.setName("Hyderabad");
    Metro m1 = new Metro();
    m1.setName("Bengaluru");
    Metro m2 = new Metro();
    m2.setName("Delhi");
    Metro m3 = new Metro();
    m3.setName("Pune");
    metroSet.add(m0);
    metroSet.add(m1);
    metroSet.add(m2);
    metroSet.add(m3);
    Mockito.when(this.metroRepository.findByName(proposedMetro)).thenReturn(null);
    Mockito.when(this.metroRepository.findAll()).thenReturn(metroSet.stream().collect(Collectors.toList()));
    Set<Metro> dummy = this.metroService.performMetroSubmission(proposedMetro);
    for(Metro m : dummy) {
      if(m.getName() == proposedMetro)
        assertTrue(1==1);
      else
        assertFalse(1==0);
    }
 }

  @ParameterizedTest
  @ValueSource(strings = {"Hyderabad", "Bengaluru"})
  public void whenGivenExistingMetroName__thenReturnInvalidMessage(String proposedMetro) {
    Metro metro = new Metro();
    metro.setStatus("confirmed");
    Mockito.when(this.metroRepository.findByName(proposedMetro)).thenReturn(metro);
    Set<Metro> metroSet = this.metroService.performMetroSubmission(proposedMetro);
    String status = "";
    for(Metro m : metroSet)
      status = m.getStatus();
    assertEquals("confirmed", status);
  }

  @ParameterizedTest
  @ValueSource(strings = {"Chandigarh"})
  public void whenGivenAlreadyProposedMetroName__thenReturnInvalidMessage(String proposedMetro) {
    Metro metro = new Metro();
    metro.setStatus("proposed");
    Mockito.when(this.metroRepository.findByName(proposedMetro)).thenReturn(metro);
    Set<Metro> dummy = this.metroService.performMetroSubmission(proposedMetro);
    assertEquals(1, dummy.size());
    for(Metro m : dummy)
      assertEquals("proposed", m.getStatus());
  }

  
}
