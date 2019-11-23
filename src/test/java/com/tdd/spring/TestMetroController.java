package com.tdd.spring;

import java.util.HashSet;
import java.util.Set;

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

@SpringBootTest
@AutoConfigureMockMvc
public class TestMetroController {

  private final MockMvc mockMvc;
  private final MetroController metroController;

  @MockBean
  private MetroService metroService;

  @MockBean
  private MetroRepository metroRepository;

  @Autowired
  public TestMetroController(MetroController metroController, MockMvc mockMvc) {
    this.metroController = metroController;
    this.mockMvc = mockMvc;
  }

  /**
   * verify that when a user hits "/metro" he gets a status 200. verify that when
   * a user hits "/metro" he gets a json response. verify the count of metros
   * returned when user hits "/metro".
   */
  @Test
  public void whenMetros__thenStatus200() throws Exception {
    Set<String> metros = new HashSet<>();
    metros.add("Hyderabad");
    metros.add("Chennai");
    metros.add("Bengaluru");
    metros.add("Kolkata");
    metros.add("Mumbai");
    Mockito.when(metroService.getMetros()).thenReturn(metros);
    mockMvc.perform(MockMvcRequestBuilders.get("/metro"))
        .andExpect(MockMvcResultMatchers.status().is(200))
        .andExpect(MockMvcResultMatchers.jsonPath("$", org.hamcrest.Matchers.hasSize(5)));
  }

  /**
   * verify when an existing metro is supplied to be added to the proposed metros,
   * an invalid message explaining the cause should be show to the user.
   * 
   * @param proposedMetro
   * @throws Exception
   */
  @ParameterizedTest
  @ValueSource(strings = { "Chennai", "Hyderabad" })
  public void whenGivenExistingMetro__thenInvalidMessage(String proposedMetro) throws Exception {
    Set<Metro> returnSet = new HashSet<Metro>();
    Metro m0 = new Metro();
    m0.setName("Hyderabad");
    m0.setName("confirmed");
    Metro m1 = new Metro();
    m0.setName("Chennai");
    m0.setName("confirmed");
    returnSet.add(m0);
    returnSet.add(m1);
    Mockito.when(this.metroService.performMetroSubmission(proposedMetro)).thenReturn(returnSet);
    mockMvc.perform(MockMvcRequestBuilders.post("/proposeMetro/{metroName}", proposedMetro))
        .andExpect(MockMvcResultMatchers.status().is(200)).andExpect(MockMvcResultMatchers.content()
            .string(proposedMetro + " is already a Metro, nothing to propose!"));
  }

  /**
   * verify when a valid new name is submitted for metro proposal, user should be
   * shown a success message.
   * 
   * @param proposedMetro
   * @throws Exception
   */
  @ParameterizedTest
  @ValueSource(strings = { "Delhi", "Pune" })
  public void whenGivenNewMetroProposal__thenSuccessMessage(String proposedMetro) throws Exception {
    Set<Metro> returnSet = new HashSet<Metro>();
    Metro m0 = new Metro();
    m0.setName("Hyderabad");
    m0.setName("confirmed");
    Metro m1 = new Metro();
    m0.setName("Bengaluru");
    m0.setName("confirmed");
    Metro m2 = new Metro();
    m0.setName("Delhi");
    m0.setName("proposed");
    Metro m3 = new Metro();
    m0.setName("Pune");
    m0.setName("proposed");
    returnSet.add(m0);
    returnSet.add(m1);
    returnSet.add(m2);
    returnSet.add(m3);
    Mockito.when(this.metroService.performMetroSubmission(proposedMetro)).thenReturn(returnSet);
    mockMvc.perform(MockMvcRequestBuilders.post("/proposeMetro/{metroName}", proposedMetro))
        .andExpect(MockMvcResultMatchers.status().is(200)).andExpect(MockMvcResultMatchers.content()
            .string(proposedMetro + " is successfully submitted for proposal."));
  }

  /**
   * verify when an already proposed metro name is submitted, user gets an
   * appropriate message
   * 
   * @param proposedMetro
   * @throws Exception
   */
  @ParameterizedTest
  @ValueSource(strings = { "Chandigarh" })
  public void whenGivenAlreadyProposedMetroName__thenReturnInvalidMessage(String proposedMetro)
      throws Exception {
    Set<Metro> returnSet = new HashSet<Metro>();
    Metro m0 = new Metro();
    m0.setName("Chandigarh");
    m0.setName("proposed");
    returnSet.add(m0);
    Mockito.when(this.metroService.performMetroSubmission(proposedMetro)).thenReturn(returnSet);
    mockMvc.perform(MockMvcRequestBuilders.post("/proposeMetro/{metroName}", proposedMetro))
        .andExpect(MockMvcResultMatchers.status().is(200)).andExpect(
            MockMvcResultMatchers.content().string(proposedMetro + " has been proposed already!!"));
  }

}
