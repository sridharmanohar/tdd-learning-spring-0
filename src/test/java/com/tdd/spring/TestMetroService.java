package com.tdd.spring;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class TestMetroService {

  @MockBean
  private MetroRepository metroRepository;

  private MetroService metroService;

  @BeforeEach
  public void setUp() {
    metroService = new MetroService(metroRepository);
  }

  /**
   * verify service layer returns a list of metros when it calls the necessary
   * repository method.
   */
  @Test
  public void verify_metros_result_set() {
    Set<String> metros = new HashSet<>();
    metros.add("Hyderabad");
    metros.add("Chennai");
    metros.add("Bengaluru");
    metros.add("Kolkata");
    metros.add("Mumbai");
    Mockito.when(metroRepository.findMetros()).thenReturn(metros);
    Assertions.assertTrue(() -> {
      Set<String> tempSet = metroService.getMetros();
      if (tempSet != null && !tempSet.isEmpty() && tempSet.size() == 5)
        return true;
      else
        return false;
    });

  }

}
