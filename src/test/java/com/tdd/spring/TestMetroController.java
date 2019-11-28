package com.tdd.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class TestMetroController {

  enum STATUS_CODE {
    OK(200), BAD_REQUEST(400);
    private int value;

    private STATUS_CODE(int value) {
      this.value = value;
    }
  }

  enum API_REQUESTS {
    GET, POST
  }

  enum API_PATHS {
    GET_METROS("/metro"), PROPOSE_METRO("/proposeMetro/");
    private String value;

    private API_PATHS(String value) {
      this.value = value;
    }
  }

  enum METROS {
    Hyderabad, Bengaluru, Chennai, Pune, Delhi, Mumbai, Kolkata, Chandigarh;
  }

  enum METRO_STATUS {
    confirmed, proposed;
  }

  private final MockMvc mockMvc;
  private final MetroController metroController;
  private final ObjectMapper objectMapper;

  @MockBean
  private MetroService metroService;

  @MockBean
  private MetroRepository metroRepository;

  @Autowired
  public TestMetroController(MetroController metroController, MockMvc mockMvc,
      ObjectMapper objectMapper) {
    this.metroController = metroController;
    this.mockMvc = mockMvc;
    this.objectMapper = objectMapper;
  }

  private List<Metro> getInputMetroList(String[][] input_array) {
    List<Metro> metroList = new ArrayList<>();
    for (int i = 0; i < input_array.length; i++) {
      for (int j = 0; j < 1; j++) {
        metroList.add(new Metro(input_array[i][j], input_array[i][j + 1]));
      }
    }
    return metroList;
  }

  private String getJSONResponseString(String action, String path) throws Exception {
    String responseString = "";
    if (action.equalsIgnoreCase("get"))
      responseString = mockMvc.perform(MockMvcRequestBuilders.get(path)).andReturn().getResponse()
          .getContentAsString();
    else if (action.equalsIgnoreCase("post"))
      responseString = mockMvc.perform(MockMvcRequestBuilders.post(path)).andReturn().getResponse()
          .getContentAsString();
    return responseString;
  }

  private List<Metro> convertJSONResponseStringToList(String jsonresponseString)
      throws JsonMappingException, JsonProcessingException {
    return this.objectMapper.readValue(jsonresponseString, new TypeReference<List<Metro>>() {
    });
  }

  private void mockMe(List<Metro> given, String[][] input_array) {
    Mockito.when(given).thenReturn(getInputMetroList(input_array));
  }

  private int getStatusCode(String action, String path) throws Exception {
    int status_code = 0;
    if (action.equalsIgnoreCase("get"))
      status_code = mockMvc.perform(MockMvcRequestBuilders.get(path)).andReturn().getResponse()
          .getStatus();
    else if (action.equalsIgnoreCase("post"))
      status_code = mockMvc.perform(MockMvcRequestBuilders.post(path)).andReturn().getResponse()
          .getStatus();
    return status_code;
  }

  private List<Metro> getMetroList(String action, String path)
      throws JsonMappingException, JsonProcessingException, Exception {
    return convertJSONResponseStringToList(getJSONResponseString(action, path));
  }

  /**
   * verify when a user hits "/metro" he gets a status 200.
   * 
   * verify when a user hits "/metro" he gets a json response.
   * 
   * verify the count of metros returned when user hits "/metro".
   */
  @Test
  public void whenMetros__thenStatus200() throws Exception {
    String[][] input_array = { { METROS.Hyderabad.name(), METRO_STATUS.confirmed.name() },
        { METROS.Chennai.name(), METRO_STATUS.confirmed.name() },
        { METROS.Bengaluru.name(), METRO_STATUS.confirmed.name() },
        { METROS.Kolkata.name(), METRO_STATUS.confirmed.name() },
        { METROS.Mumbai.name(), METRO_STATUS.confirmed.name() } };
    mockMe(metroService.getMetros(), input_array);
    assertEquals(STATUS_CODE.OK.value,
        getStatusCode(API_REQUESTS.GET.name(), API_PATHS.GET_METROS.value));
    assertEquals(5, getMetroList(API_REQUESTS.GET.name(), API_PATHS.GET_METROS.value).size());
  }

  /**
   * verify when an existing metro with a status confirmed is supplied to be added
   * to the proposed metros list, a BAD_REQUEST (400) is returned.
   * 
   * also, verify the response contains the supplied metros name and status.
   * 
   * @param proposedMetro
   * @throws Exception
   */
  @ParameterizedTest
  @EnumSource(value = METROS.class, names = { "Hyderabad", "Mumbai" })
  public void whenGivenExistingMetro__thenInvalidMessage(METROS argument) throws Exception {
    String[][] input_array = { { argument.name(), METRO_STATUS.confirmed.name() } };

    mockMe(this.metroService.performMetroSubmission(argument.name()), input_array);

    assertEquals(STATUS_CODE.BAD_REQUEST.value,
        getStatusCode(API_REQUESTS.POST.name(), API_PATHS.PROPOSE_METRO.value + argument.name()));

    List<Metro> metroList = getMetroList(API_REQUESTS.POST.name(),
        API_PATHS.PROPOSE_METRO.value + argument.name());

    assertTrue(metroList.size() == 1);

    boolean match = false;

    for (Metro m : metroList) {
      if (argument.name().equalsIgnoreCase(m.getName())
          && METRO_STATUS.confirmed.name().equalsIgnoreCase(m.getStatus()))
        match = true;
    }

    assertTrue(match);
  }

  /**
   * verify when a valid new city is submitted for metro proposal, user should get
   * a OK (200) status code.
   * 
   * verify when a valid new city is submitted for metro proposal, user, in
   * response, should get the list of already existing metrors and the new metro
   * along with their status codes.
   * 
   * @param proposedMetro
   * @throws Exception
   */
  @ParameterizedTest
  @EnumSource(value = METROS.class, names = { "Delhi", "Pune" })
  public void whenGivenNewMetroProposal__thenSuccessMessage(METROS argument) throws Exception {
    String[][] input_array = { { METROS.Hyderabad.name(), METRO_STATUS.confirmed.name() },
        { METROS.Bengaluru.name(), METRO_STATUS.confirmed.name() },
        { argument.name(), METRO_STATUS.proposed.name() } };

    mockMe(this.metroService.performMetroSubmission(argument.name()), input_array);

    assertEquals(STATUS_CODE.OK.value,
        getStatusCode(API_REQUESTS.POST.name(), API_PATHS.PROPOSE_METRO.value + argument.name()));

    List<Metro> listMetros = getMetroList(API_REQUESTS.POST.name(),
        API_PATHS.PROPOSE_METRO.value + argument.name());

    assertEquals(3, listMetros.size());

    boolean match = false;
    for (Metro m : listMetros) {
      if (argument.name().equalsIgnoreCase(m.getName())
          && METRO_STATUS.proposed.name().equalsIgnoreCase(m.getStatus()))
        match = true;
    }

    assertTrue(match);
  }

  /**
   * verify when an already proposed metro name is submitted, user will get a
   * BAD_REQUEST (400) status code.
   * 
   * verify when an already proposed metro name is submitted, the response
   * contains the name of the metro along with it's status.
   * 
   * @param proposedMetro
   * @throws Exception
   */
  @ParameterizedTest
  @EnumSource(value = METROS.class, names = { "Chandigarh" })
  public void whenGivenAlreadyProposedMetroName__thenReturnInvalidMessage(METROS argument)
      throws Exception {
    String[][] input_array = { { argument.name(), METRO_STATUS.proposed.name() } };

    mockMe(this.metroService.performMetroSubmission(argument.name()), input_array);

    assertEquals(STATUS_CODE.BAD_REQUEST.value,
        getStatusCode(API_REQUESTS.POST.name(), API_PATHS.PROPOSE_METRO.value + argument.name()));

    List<Metro> metroList = getMetroList(API_REQUESTS.POST.name(),
        API_PATHS.PROPOSE_METRO.value + argument.name());

    assertTrue(metroList.size() == 1);

    boolean nameMatch = false;
    boolean statusMatch = false;

    for (Metro m : metroList) {
      if (argument.name().equalsIgnoreCase(m.getName()))
        nameMatch = true;
      if (METRO_STATUS.proposed.name().equalsIgnoreCase(m.getStatus()))
        statusMatch = true;
    }

    assertTrue(nameMatch);
    assertTrue(statusMatch);
  }

}
