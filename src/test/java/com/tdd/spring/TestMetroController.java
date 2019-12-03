package com.tdd.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class TestMetroController extends TestMetroSuper {

  private final MetroController metroController;
  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;

  @MockBean
  private MetroService metroService;

  @MockBean
  private MetroRepository metroRepository;

  @Autowired
  public TestMetroController(MetroController metroController, MockMvc mockMvc,
      ObjectMapper objectMapper) {
    super(mockMvc, objectMapper);
    this.mockMvc = mockMvc;
    this.objectMapper = objectMapper;
    this.metroController = metroController;
  }

  @Test
  public void whenGetMetroRequest__thenResponseStatusShouldbe200() throws Exception {
    String[][] confirmedMetros = { { METRO.HYDERABAD.value, METROSTATUS.CONFIRMED.value },
        { METRO.CHENNAI.value, METROSTATUS.CONFIRMED.value },
        { METRO.BENGALURU.value, METROSTATUS.CONFIRMED.value },
        { METRO.KOLKATA.value, METROSTATUS.CONFIRMED.value },
        { METRO.MUMBAI.value, METROSTATUS.CONFIRMED.value } };

    mockThisCall(metroService.getMetros(), confirmedMetros);

    assertEquals(HTTPSTATUS.OK.value,
        getStatusCode(HTTPREQUEST.GET.name(), ACTION.GETMETROS.value));
  }

  @Test
  public void whenGetMetroRequest__thenReturnedMetroListSizeMatchesTotalExistingMetros()
      throws Exception {
    String[][] confimedMetros = { { METRO.HYDERABAD.value, METROSTATUS.CONFIRMED.value },
        { METRO.CHENNAI.value, METROSTATUS.CONFIRMED.value },
        { METRO.BENGALURU.value, METROSTATUS.CONFIRMED.value },
        { METRO.KOLKATA.value, METROSTATUS.CONFIRMED.value },
        { METRO.MUMBAI.value, METROSTATUS.CONFIRMED.value } };

    mockThisCall(metroService.getMetros(), confimedMetros);

    assertEquals(5, getMetroListSize(HTTPREQUEST.GET.name(), ACTION.GETMETROS.value));
  }

  @ParameterizedTest
  @EnumSource(value = METRO.class, names = { "HYDERABAD", "MUMBAI" })
  public void whenConfirmedMetroSubmittedForProposal__thenResponseStatus_IS_400(METRO argument)
      throws Exception {
    String[][] existingConfirmedMetroFromDatabase = {
        { argument.value, METROSTATUS.CONFIRMED.value } };

    mockThisCall(this.metroService.performMetroSubmission(argument.value),
        existingConfirmedMetroFromDatabase);

    assertEquals(HTTPSTATUS.BADREQUEST.value,
        getStatusCode(HTTPREQUEST.POST.name(), ACTION.PROPOSEMETRO.value + argument.value));
  }

  @ParameterizedTest
  @EnumSource(value = METRO.class, names = { "HYDERABAD", "MUMBAI" })
  public void whenConfirmedMetroSubmittedForProposal__thenReturnedMetroListSizeIsOne(METRO argument)
      throws Exception {
    String[][] existingConfirmedMetroFromDatabase = {
        { argument.value, METROSTATUS.CONFIRMED.value } };

    mockThisCall(this.metroService.performMetroSubmission(argument.value),
        existingConfirmedMetroFromDatabase);

    List<Metro> metroList = getMetroList(HTTPREQUEST.POST.name(),
        ACTION.PROPOSEMETRO.value + argument.value);

    assertTrue(metroList.size() == 1);
  }

  @ParameterizedTest
  @EnumSource(value = METRO.class, names = { "HYDERABAD", "MUMBAI" })
  public void whenConfirmedMetroSubmittedForProposal__thenReturnedMetroList_contains_suppliedMetro(
      METRO argument) throws Exception {
    String[][] existingConfirmedMetroFromDatabase = {
        { argument.value, METROSTATUS.CONFIRMED.value } };

    mockThisCall(this.metroService.performMetroSubmission(argument.value),
        existingConfirmedMetroFromDatabase);

    List<Metro> metroList = getMetroList(HTTPREQUEST.POST.name(),
        ACTION.PROPOSEMETRO.value + argument.value);

    long count = verifyIfMetroList__contains__suppliedNameandStatus(argument.value,
        METROSTATUS.CONFIRMED.value, metroList);

    assertTrue(1 == count);
  }

  @ParameterizedTest
  @EnumSource(value = METRO.class, names = { "DELHI", "PUNE" })
  public void whenNewMetroSubmittedForProposal__thenResponseStatusIs200(METRO argument)
      throws Exception {
    String[][] allMetrosInSystemAfterSubmission = {
        { METRO.HYDERABAD.value, METROSTATUS.CONFIRMED.value },
        { METRO.BENGALURU.value, METROSTATUS.CONFIRMED.value },
        { argument.value, METROSTATUS.PROPOSED.value } };

    mockThisCall(this.metroService.performMetroSubmission(argument.value),
        allMetrosInSystemAfterSubmission);

    assertEquals(HTTPSTATUS.OK.value,
        getStatusCode(HTTPREQUEST.POST.name(), ACTION.PROPOSEMETRO.value + argument.value));
  }

  @ParameterizedTest
  @EnumSource(value = METRO.class, names = { "DELHI", "PUNE" })
  public void whenNewMetroSubmittedForProposal__thenReturnedMetroListSizeMatchesTotalExistingMetrosPlusNewMetro(
      METRO argument) throws Exception {
    String[][] allMetrosInSystemAfterSubmission = {
        { METRO.HYDERABAD.value, METROSTATUS.CONFIRMED.value },
        { METRO.BENGALURU.value, METROSTATUS.CONFIRMED.value },
        { argument.value, METROSTATUS.PROPOSED.value } };

    mockThisCall(this.metroService.performMetroSubmission(argument.value),
        allMetrosInSystemAfterSubmission);

    List<Metro> listMetros = getMetroList(HTTPREQUEST.POST.name(),
        ACTION.PROPOSEMETRO.value + argument.value);

    assertEquals(3, listMetros.size());
  }

  @ParameterizedTest
  @EnumSource(value = METRO.class, names = { "DELHI", "PUNE" })
  public void whenNewMetroSubmittedForProposal__thenReturnedMetroListShouldContainSuppliedNewMetroAlso( 
      METRO argument) throws Exception {
    String[][] allMetrosInSystemAfterSubmission = {
        { METRO.HYDERABAD.value, METROSTATUS.CONFIRMED.value },
        { METRO.BENGALURU.value, METROSTATUS.CONFIRMED.value },
        { argument.value, METROSTATUS.PROPOSED.value } };

    mockThisCall(this.metroService.performMetroSubmission(argument.value),
        allMetrosInSystemAfterSubmission);

    List<Metro> listMetros = getMetroList(HTTPREQUEST.POST.name(),
        ACTION.PROPOSEMETRO.value + argument.value);

    long count = verifyIfMetroList__contains__suppliedNameandStatus(argument.value,
        METROSTATUS.PROPOSED.value, listMetros);

    assertTrue(1 == count);
  }

  @ParameterizedTest
  @EnumSource(value = METRO.class, names = { "CHANDIGARH" })
  public void whenProposedMetroSubmittedForProposal__thenResponseStatus_IS_400(METRO argument)
      throws Exception {
    String[][] existingProposedMetroFromDB = { { argument.value, METROSTATUS.PROPOSED.value } };

    mockThisCall(this.metroService.performMetroSubmission(argument.value),
        existingProposedMetroFromDB);

    assertEquals(HTTPSTATUS.BADREQUEST.value,
        getStatusCode(HTTPREQUEST.POST.name(), ACTION.PROPOSEMETRO.value + argument.value));
  }

  @ParameterizedTest
  @EnumSource(value = METRO.class, names = { "CHANDIGARH" })
  public void whenProposedMetroSubmittedForProposal__thenReturnedMetroListSizeIsOne(METRO argument)
      throws Exception {
    String[][] existingProposedMetroFromDB = { { argument.value, METROSTATUS.PROPOSED.value } };

    mockThisCall(this.metroService.performMetroSubmission(argument.value),
        existingProposedMetroFromDB);

    List<Metro> metroList = getMetroList(HTTPREQUEST.POST.name(),
        ACTION.PROPOSEMETRO.value + argument.value);

    assertTrue(metroList.size() == 1);
  }

  @ParameterizedTest
  @EnumSource(value = METRO.class, names = { "CHANDIGARH" })
  public void whenProposedMetroSubmittedForProposal__thenReturnedMetroListShouldContainSuppliedMetro(
      METRO argument) throws Exception {
    String[][] existingProposedMetroFromDB = { { argument.value, METROSTATUS.PROPOSED.value } };

    mockThisCall(this.metroService.performMetroSubmission(argument.value),
        existingProposedMetroFromDB);

    List<Metro> metroList = getMetroList(HTTPREQUEST.POST.name(),
        ACTION.PROPOSEMETRO.value + argument.value);

    long count = verifyIfMetroList__contains__suppliedNameandStatus(argument.value,
        METROSTATUS.PROPOSED.value, metroList);

    assertTrue(1 == count);
  }

}