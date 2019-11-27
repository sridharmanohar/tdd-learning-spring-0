package com.tdd.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class TestMetro {

  private final MockMvc mockMvc;
  private final MetroController metroController;
  private final ObjectMapper objectMapper;

  @Autowired
  public TestMetro(MetroController metroController, MockMvc mockMvc, ObjectMapper objectMapper) {
    this.metroController = metroController;
    this.mockMvc = mockMvc;
    this.objectMapper = objectMapper;
  }

  /**
   * verify the status, result size and the content from the database when a
   * /metro request occurs.
   */
  @Test
  public void whenMetrosRequest__thenStatus200() throws Exception {
    // Body = ["Chennai","Bengaluru","Kolkata","Mumbai","Hyderabad"] - this is how
    // the response in the body will be.
    final String EXPECTED_BODY_RESPONSE = "[\"Chennai\",\"Bengaluru\",\"Kolkata\",\"Mumbai\",\"Hyderabad\"]";
    System.out.println(" ----------------- IN HERE -----------------------");
    mockMvc.perform(MockMvcRequestBuilders.get("/metro"))
        .andExpect(MockMvcResultMatchers.status().is(200))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(5)))
        .andExpect(MockMvcResultMatchers.content().string(EXPECTED_BODY_RESPONSE));
  }

  
  @ParameterizedTest
  @ValueSource(strings = { "Pune" })
  @Transactional
  public void whenGivenNewMetroProposal__thenReturnSuccessMessage(String proposedMetro)
      throws Exception {

    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.post("/proposeMetro/{metroName}", proposedMetro))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    String contentString = result.getResponse().getContentAsString();

    List<Metro> metroList = this.objectMapper.readValue(contentString,
        new TypeReference<List<Metro>>() {
        });

    boolean found = false;

    for (Metro m : metroList) {
      if (m.getName().equalsIgnoreCase(proposedMetro)) {
        found = true;
      }
    }

    assertTrue(found);
  }

  
  @ParameterizedTest
  @ValueSource(strings = {"Hyderabad"})
  @Transactional
  public void whenGivenExistingMetro__thenReturnInvalidMessage(String proposedMetro)
      throws Exception {
    String contentString = mockMvc.perform(MockMvcRequestBuilders.post("/proposeMetro/{metroName}", proposedMetro))
        .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn().getResponse().getContentAsString();
    List<Metro> metroList = this.objectMapper.readValue(contentString, new TypeReference<List<Metro>>() {});
    assertEquals(1, metroList.size());
    for(Metro m : metroList) {
      assertEquals("confirmed", m.getStatus());
      assertEquals(proposedMetro, m.getName());
    }
  }

  
  @ParameterizedTest
  @ValueSource(strings = {"Delhi"})
  @Transactional
  public void whenGivenProposedMetro__thenReturnInvalidMessage(String proposedMetro)
      throws Exception {
    String contentString = mockMvc.perform(MockMvcRequestBuilders.post("/proposeMetro/{metroName}", proposedMetro))
        .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn().getResponse().getContentAsString();
    List<Metro> metroList = this.objectMapper.readValue(contentString, new TypeReference<List<Metro>>() {});
    assertEquals(1, metroList.size());
    for(Metro m : metroList) {
      assertEquals("proposed", m.getStatus());
      assertEquals(proposedMetro, m.getName());
    }
  }

}
