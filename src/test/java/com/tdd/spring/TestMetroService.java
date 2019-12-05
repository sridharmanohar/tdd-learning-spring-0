package com.tdd.spring;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class TestMetroService extends TestMetroSuper {

  private final MetroService metroService;

  @MockBean
  private MetroRepository metroRepository;

  @Autowired
  public TestMetroService(MetroService metroService) {
    super();
    this.metroService = metroService;
  }

  
  // THIS NEEDS TO REFACTORED LIKE OTHERS !
  @ParameterizedTest
  @EnumSource(value = METRO.class, names = {"DELHI", "PUNE"})
  public void whenNewMetroProposalSubmitted__thenReturnedMetroListShouldContainSuppliedMetroAlso(METRO proposedMetro) {
    String[][] metroArray = {
        {METRO.HYDERABAD.value, METROSTATUS.CONFIRMED.value},
        {METRO.BENGALURU.value, METROSTATUS.CONFIRMED.value},
        {proposedMetro.value, METROSTATUS.CONFIRMED.value}
    };
    
    mockThisCall(this.metroRepository.findByName(proposedMetro.value), null);
    mockThisCall(this.metroRepository.findAll(), metroArray);
    
    List<Metro> metrosInSystemAfterNewMetroSubmission = this.metroService.performMetroSubmission(proposedMetro.value);
    
    long matchedMetros = metrosInSystemAfterNewMetroSubmission.stream().filter(m -> {
      return m.getName().equalsIgnoreCase(proposedMetro.value);
    }).count();
    
    assertTrue(matchedMetros == 1);
    
 }

  @ParameterizedTest
  @EnumSource(value = METRO.class, names = {"HYDERABAD", "BENGALURU"})
  public void whenAlreadyConfirmedMetroSubmittedAgain__thenReturnedListShouldContainOnlySuppliedMetro(METRO proposedMetro) {
    List<Metro> metroList =  prepareData(proposedMetro.value, METROSTATUS.CONFIRMED.value);    
    long matchedMetros = metroList.stream().filter(m -> {
      return m.getName().equalsIgnoreCase(proposedMetro.value) && m.getStatus().equalsIgnoreCase(METROSTATUS.CONFIRMED.value);
    }).count();
    
    assertTrue(matchedMetros == 1);
  }

  @ParameterizedTest
  @EnumSource(value = METRO.class, names = {"HYDERABAD", "BENGALURU"})
  public void whenAlreadyConfirmedMetroSubmittedAgain__thenReturnedListSizeShouldBeOne(METRO proposedMetro) {
    List<Metro> metroList =  prepareData(proposedMetro.value, METROSTATUS.CONFIRMED.value);
    assertTrue(metroList.size() == 1);
  }
  
  
  @ParameterizedTest
  @EnumSource(value = METRO.class, names = {"CHANDIGARH"})
  public void whenAlreadyProposedMetroSubmittedAgain__thenReturnListShouldContainSuppliedMetro(METRO proposedMetro) {
    List<Metro> metroList =  prepareData(proposedMetro.value, METROSTATUS.PROPOSED.value);
    
    long matchedMetros = metroList.stream().filter(m -> {
      return m.getName().equalsIgnoreCase(proposedMetro.value) && m.getStatus().equalsIgnoreCase(METROSTATUS.PROPOSED.value);
    }).count();
    
    assertTrue(matchedMetros == 1);
  }

  @ParameterizedTest
  @EnumSource(value = METRO.class, names = {"CHANDIGARH"})
  public void whenAlreadyProposedMetroSubmittedAgain__thenReturnListShouldSizeShouldBeOne(METRO proposedMetro) {
    List<Metro> metroList =  prepareData(proposedMetro.value, METROSTATUS.PROPOSED.value);
    
    assertTrue(metroList.size() == 1);
  }

  // NAME THIS TO SOMETHING BETTER, MAYBE.
  private List<Metro> prepareData(String metroName, String metroStatus) {
    String[][] metroArray = {
        {metroName, metroStatus}  
    };
    mockThisCall(this.metroRepository.findByName(metroName), metroArray);
    List<Metro> metroList = this.metroService.performMetroSubmission(metroName);
    return metroList;
  }
  
}
