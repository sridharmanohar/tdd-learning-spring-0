package com.tdd.spring;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
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

}
