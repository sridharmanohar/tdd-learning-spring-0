package com.tdd.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class TestMetroController {

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

  private String getJSONResponseString(String action, String path, int status) throws Exception {
    String responseString = "";

    if (action.equalsIgnoreCase("get"))
      responseString = mockMvc.perform(MockMvcRequestBuilders.get(path))
          .andExpect(MockMvcResultMatchers.status().is(status)).andReturn().getResponse()
          .getContentAsString();
    else if (action.equalsIgnoreCase("post"))
      responseString = mockMvc.perform(MockMvcRequestBuilders.post(path))
          .andExpect(MockMvcResultMatchers.status().is(status)).andReturn().getResponse()
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

  /**
   * verify when a user hits "/metro" he gets a status 200.
   * 
   * verify when a user hits "/metro" he gets a json response.
   * 
   * verify the count of metros returned when user hits "/metro".
   */
  @Test
  public void whenMetros__thenStatus200() throws Exception {
    String[][] input_array = { { "Hyderabad", "confirmed" }, { "Chennai", "confirmed" },
        { "Bengaluru", "confirmed" }, { "Kolkata", "confirmed" }, { "Mumbai", "confirmed" } };
    mockMe(metroService.getMetros(), input_array);
    assertEquals(5,
        convertJSONResponseStringToList(getJSONResponseString("get", "/metro", 200)).size());
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
  @ValueSource(strings = { "Hyderabad", "Mumbai" })
  public void whenGivenExistingMetro__thenInvalidMessage(String proposedMetro) throws Exception {
    String[][] input_array = { { proposedMetro, "confirmed" } };

    mockMe(this.metroService.performMetroSubmission(proposedMetro), input_array);

    List<Metro> metroList = convertJSONResponseStringToList(
        getJSONResponseString("post", "/proposeMetro/" + proposedMetro, 400));

    assertTrue(metroList.size() == 1);

    boolean match = false;

    for (Metro m : metroList) {
      if (proposedMetro.equalsIgnoreCase(m.getName())
          && "confirmed".equalsIgnoreCase(m.getStatus()))
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
  @ValueSource(strings = { "Delhi", "Pune" })
  public void whenGivenNewMetroProposal__thenSuccessMessage(String proposedMetro) throws Exception {
    String[][] input_array = { { "Hyderabad", "confirmed" }, { "Bengaluru", "confirmed" },
        { proposedMetro, "proposed" }, };

    mockMe(this.metroService.performMetroSubmission(proposedMetro), input_array);

    List<Metro> listMetros = convertJSONResponseStringToList(
        getJSONResponseString("post", "/proposeMetro/" + proposedMetro, 200));

    assertEquals(3, listMetros.size());

    boolean match = false;

    for (Metro m : listMetros) {
      if (proposedMetro.equalsIgnoreCase(m.getName()) && "proposed".equalsIgnoreCase(m.getStatus()))
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
  @ValueSource(strings = { "Chandigarh" })
  public void whenGivenAlreadyProposedMetroName__thenReturnInvalidMessage(String proposedMetro)
      throws Exception {
    String[][] input_array = { { proposedMetro, "proposed" } };

    mockMe(this.metroService.performMetroSubmission(proposedMetro), input_array);

    List<Metro> metroList = convertJSONResponseStringToList(
        getJSONResponseString("post", "/proposeMetro/" + proposedMetro, 400));

    assertTrue(metroList.size() == 1);

    boolean nameMatch = false;
    boolean statusMatch = false;

    for (Metro m : metroList) {
      if (proposedMetro.equalsIgnoreCase(m.getName()))
        nameMatch = true;
      if ("proposed".equalsIgnoreCase(m.getStatus()))
        statusMatch = true;
    }

    assertTrue(nameMatch);
    assertTrue(statusMatch);
  }

}
