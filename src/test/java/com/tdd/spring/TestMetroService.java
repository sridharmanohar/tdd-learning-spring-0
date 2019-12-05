package com.tdd.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class TestMetroService {

  enum METRO {
    HYDERABAD("Hyderabad"), BENGALURU("Bengaluru"), CHENNAI("Chennai"), PUNE("Pune"),
    DELHI("Delhi"), MUMBAI("Mumbai"), KOLKATA("Kolkata"), CHANDIGARH("Chandigarh");
    protected String value;

    private METRO(String name) {
      this.value = name;
    }

  }

  enum METROSTATUS {
    CONFIRMED("confirmed"), PROPOSED("proposed");
    protected String value;

    private METROSTATUS(String name) {
      this.value = name;
    }

  }
  
  
  private final MetroService metroService;

  @MockBean
  private MetroRepository metroRepository;

  @Autowired
  public TestMetroService(MetroService metroService) {
    this.metroService = metroService;
  }

  
  @ParameterizedTest
//  @ValueSource(strings = {"Delhi", "Pune"})
  @EnumSource(value = METRO.class, names = {"DELHI", "PUNE"})
  public void whenNewMetroProposalSubmitted__thenReturnedMetroListShouldContainSuppliedMetroAlso(METRO proposedMetro) {
    String[][] metroArray = {
        {METRO.HYDERABAD.value, METROSTATUS.CONFIRMED.value},
        {METRO.BENGALURU.value, METROSTATUS.CONFIRMED.value},
        {proposedMetro.value, METROSTATUS.CONFIRMED.value}
    };
    
    List<Metro> metroList = convertArrayToList(metroArray);
    
    Mockito.when(this.metroRepository.findByName(proposedMetro.value)).thenReturn(null);
    Mockito.when(this.metroRepository.findAll()).thenReturn(metroList);
    List<Metro> metrosInSystemAfterNewMetroSubmission = this.metroService.performMetroSubmission(proposedMetro);
    
    long matchedMetros = metrosInSystemAfterNewMetroSubmission.stream().filter(m -> {
      return m.getName().equalsIgnoreCase(proposedMetro);
    }).count();
    
    assertTrue(matchedMetros == 1);
    
 }

  private List<Metro> convertArrayToList(String[][] input_array) {
    List<Metro> metroList = new ArrayList<>();
    for (int i = 0; i < input_array.length; i++) {
      for (int j = 0; j < 1; j++) {
        metroList.add(new Metro(input_array[i][j], input_array[i][j + 1]));
      }
    }
    return metroList;
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
