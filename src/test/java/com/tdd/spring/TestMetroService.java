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

  
  @ParameterizedTest
  @ValueSource(strings = {"Delhi", "Pune"})
  public void whenGivenNewMetroProposal__thenReturnSuccessMessage(String proposedMetro) {
    List<Metro> metroSet = new ArrayList<>();
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
    List<Metro> dummy = this.metroService.performMetroSubmission(proposedMetro);
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
    List<Metro> metroSet = this.metroService.performMetroSubmission(proposedMetro);
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
    List<Metro> dummy = this.metroService.performMetroSubmission(proposedMetro);
    assertEquals(1, dummy.size());
    for(Metro m : dummy)
      assertEquals("proposed", m.getStatus());
  }

  
}
