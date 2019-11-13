package com.tdd.spring;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

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
public class TestMyController {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MyService myService;

  private MyController myController = new MyController(myService);

  /**
   * verify creation of mycontroller by application context.
   */
  @Test
  public void assertApplicationContext() {
    assertNotNull(myController);
  }

  /**
   * verify welcome message.
   */
  @Test
  public void whenHome__thenDefaultWelcomeMessage() throws Exception {
    Mockito.when(myService.greet()).thenReturn("Hello World!");
    mockMvc.perform(MockMvcRequestBuilders.get("/"))
        .andExpect(MockMvcResultMatchers.content().string("Hello World!"));
  }

  /**
   * verify login view is returned for login request.
   */
  @Test
  public void whenLogin__thenLoginView() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/login"))
        .andExpect(MockMvcResultMatchers.view().name("login.html"));
  }

  /**
   * verify the GET for /metros will return metros
   */
  @Test
  public void whenMetros__thenDisplayMapContents() throws Exception {
    Map<String, String> mockMetroMap = new HashMap<>();
    mockMetroMap.put("Bengaluru", "Confirmed");
    mockMetroMap.put("Chennai", "Confirmed");
    mockMetroMap.put("Kolkata", "Confirmed");
    mockMetroMap.put("Mumbai", "Confirmed");
    mockMetroMap.put("Delhi", "Confirmed");
    mockMetroMap.put("Hyderabad", "Confirmed");
    Mockito.when(myService.getMetros()).thenReturn(mockMetroMap);
    mockMvc.perform(MockMvcRequestBuilders.get("/metros"))
        .andExpect(MockMvcResultMatchers.content().string(
            "{\"Delhi\":\"Confirmed\",\"Bengaluru\":\"Confirmed\",\"Chennai\":\"Confirmed\",\"Kolkata\":\"Confirmed\",\"Mumbai\":\"Confirmed\",\"Hyderabad\":\"Confirmed\"}"));
  }

}
