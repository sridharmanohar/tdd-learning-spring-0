package com.tdd.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class TestMetroController {

  enum HTTPSTATUS {
    OK(200), BADREQUEST(400);
    private int value;

    private HTTPSTATUS(int value) {
      this.value = value;
    }
  }

  enum HTTPREQUEST {
    GET, POST
  }

  enum ACTION {
    GETMETROS("/metro"), PROPOSEMETRO("/proposeMetro/");
    private String value;

    private ACTION(String value) {
      this.value = value;
    }
  }

  enum METRO {
    HYDERABAD("Hyderabad"), BENGALURU("Bengaluru"), CHENNAI("Chennai"), PUNE("Pune"),
    DELHI("Delhi"), MUMBAI("Mumbai"), KOLKATA("Kolkata"), CHANDIGARH("Chandigarh");
    private String value;

    private METRO(String name) {
      this.value = name;
    }

  }

  enum METROSTATUS {
    CONFIRMED("confirmed"), PROPOSED("proposed");
    private String value;

    private METROSTATUS(String name) {
      this.value = name;
    }

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

  /**
   * verify when a user hits "/metro" he gets a status 200.
   * 
   * verify when a user hits "/metro" he gets a json response.
   * 
   * verify the count of metros returned when user hits "/metro".
   */
  @Test
  public void whenMetros__thenStatus200() throws Exception {
    String[][] input_array = { { METRO.HYDERABAD.value, METROSTATUS.CONFIRMED.value },
        { METRO.CHENNAI.value, METROSTATUS.CONFIRMED.value },
        { METRO.BENGALURU.value, METROSTATUS.CONFIRMED.value },
        { METRO.KOLKATA.value, METROSTATUS.CONFIRMED.value },
        { METRO.MUMBAI.value, METROSTATUS.CONFIRMED.value } };

    mockThisCall(metroService.getMetros(), input_array);

    int status_code = getStatusCode(HTTPREQUEST.GET.name(), ACTION.GETMETROS.value);

    assertEquals(HTTPSTATUS.OK.value, status_code);
    assertEquals(5, getMetroListSize(HTTPREQUEST.GET.name(), ACTION.GETMETROS.value));
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
  @EnumSource(value = METRO.class, names = { "HYDERABAD", "MUMBAI" })
  public void whenGivenExistingMetro__thenInvalidMessage(METRO argument) throws Exception {
    String[][] input_array = { { argument.value, METROSTATUS.CONFIRMED.value } };

    mockThisCall(this.metroService.performMetroSubmission(argument.value), input_array);

    List<Metro> metroList = getMetroList(HTTPREQUEST.POST.name(),
        ACTION.PROPOSEMETRO.value + argument.value);

    long count = verifyIfMetroList__contains__suppliedNameandStatus(argument.value,
        METROSTATUS.CONFIRMED.value, metroList);

    assertEquals(HTTPSTATUS.BADREQUEST.value,
        getStatusCode(HTTPREQUEST.POST.name(), ACTION.PROPOSEMETRO.value + argument.value));
    assertTrue(metroList.size() == 1);
    assertTrue(1 == count);
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
  @EnumSource(value = METRO.class, names = { "DELHI", "PUNE" })
  public void whenGivenNewMetroProposal__thenSuccessMessage(METRO argument) throws Exception {
    String[][] input_array = { { METRO.HYDERABAD.value, METROSTATUS.CONFIRMED.value },
        { METRO.BENGALURU.value, METROSTATUS.CONFIRMED.value },
        { argument.value, METROSTATUS.PROPOSED.value } };

    mockThisCall(this.metroService.performMetroSubmission(argument.value), input_array);

    List<Metro> listMetros = getMetroList(HTTPREQUEST.POST.name(),
        ACTION.PROPOSEMETRO.value + argument.value);

    long count = verifyIfMetroList__contains__suppliedNameandStatus(argument.value,
        METROSTATUS.PROPOSED.value, listMetros);

    assertEquals(HTTPSTATUS.OK.value,
        getStatusCode(HTTPREQUEST.POST.name(), ACTION.PROPOSEMETRO.value + argument.value));
    assertEquals(3, listMetros.size());
    assertTrue(1 == count);
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
  @EnumSource(value = METRO.class, names = { "CHANDIGARH" })
  public void whenGivenAlreadyProposedMetroName__thenReturnInvalidMessage(METRO argument)
      throws Exception {
    String[][] input_array = { { argument.value, METROSTATUS.PROPOSED.value } };

    mockThisCall(this.metroService.performMetroSubmission(argument.value), input_array);

    List<Metro> metroList = getMetroList(HTTPREQUEST.POST.name(),
        ACTION.PROPOSEMETRO.value + argument.value);

    long count = verifyIfMetroList__contains__suppliedNameandStatus(argument.value,
        METROSTATUS.PROPOSED.value, metroList);

    assertEquals(HTTPSTATUS.BADREQUEST.value,
        getStatusCode(HTTPREQUEST.POST.name(), ACTION.PROPOSEMETRO.value + argument.value));
    assertTrue(metroList.size() == 1);
    assertTrue(1 == count);
  }

  /**
   * converting the supplied mutli-dimensional array, containing metro name and
   * status is converted into a list of Metro objects.
   * 
   * @param input_array
   * @return
   */
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
    String responseString = action.equalsIgnoreCase("get")
        ? mockMvc.perform(MockMvcRequestBuilders.get(path)).andReturn().getResponse()
            .getContentAsString()
        : mockMvc.perform(MockMvcRequestBuilders.post(path)).andReturn().getResponse()
            .getContentAsString();

    return responseString;
  }

  private List<Metro> convertJSONResponseStringToList(String jsonresponseString)
      throws JsonMappingException, JsonProcessingException {
    return this.objectMapper.readValue(jsonresponseString, new TypeReference<List<Metro>>() {
    });
  }

  private void mockThisCall(List<Metro> given, String[][] input_array) {
    Mockito.when(given).thenReturn(getInputMetroList(input_array));
  }

  private int getStatusCode(String action, String path) throws Exception {
    System.out.println("---------------- in status code -------------");
    System.out.println(action);
    System.out.println(path);
    int status_code = action.equalsIgnoreCase("get")
        ? mockMvc.perform(MockMvcRequestBuilders.get(path)).andReturn().getResponse().getStatus()
        : mockMvc.perform(MockMvcRequestBuilders.post(path)).andReturn().getResponse().getStatus();

    return status_code;
  }

  private List<Metro> getMetroList(String action, String path)
      throws JsonMappingException, JsonProcessingException, Exception {
    return convertJSONResponseStringToList(getJSONResponseString(action, path));
  }

  private int getMetroListSize(String requestType, String action)
      throws JsonMappingException, JsonProcessingException, Exception {
    return getMetroList(requestType, action).size();
  }

  private long verifyIfMetroList__contains__suppliedNameandStatus(String metroName,
      String metroStatus, List<Metro> metroList) {
    long count = metroList.stream().filter(m -> {
      return m.getName().equalsIgnoreCase(metroName) ? m.getStatus().equalsIgnoreCase(metroStatus)
          : false;
    }).count();

    return count;
  }

}