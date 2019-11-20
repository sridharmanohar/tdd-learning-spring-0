package com.tdd.spring;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
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

  @Autowired
  private MockMvc mockMvc;
  
  private MetroController metroController;
  private MetroService metroService;
  private MetroRepository metroRepository;
  
  @BeforeEach
  public void setp() {
    metroRepository = new MetroRepository() {
      @Override
      public Set<String> findMetros() {
        Set<String> metros = new HashSet<>();
        metros.add("Hyderabad");
        metros.add("Chennai");
        metros.add("Bengaluru");
        metros.add("Kolkata");
        metros.add("Mumbai");
        // TODO Auto-generated method stub
        return metros;
      }
    };
    metroService = new MetroService(metroRepository);
    metroController = new MetroController(metroService);
  }
    
  @Test
  public void whenMetrosRequest__thenStatus200() throws Exception {
    System.out.println(" ----------------- IN HERE -----------------------");
    mockMvc.perform(MockMvcRequestBuilders.get("/metro"))
        .andExpect(MockMvcResultMatchers.status().is(200));
  }
}
