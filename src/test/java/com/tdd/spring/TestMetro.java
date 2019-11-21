package com.tdd.spring;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class TestMetro {

  private final MockMvc mockMvc;
  private final MetroController metroController;

  @Autowired
  public TestMetro(MetroController metroController, MockMvc mockMvc) {
    this.metroController = metroController;
    this.mockMvc = mockMvc;
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
}
