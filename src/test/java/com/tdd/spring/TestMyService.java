package com.tdd.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestMyService {

  private MyService myService = new MyService();

  /**
   * verify initial metro list is not-empty
   */
  @Test
  public void veifyInitialMapSizeGreaterThanZero() throws Exception {
    assertTrue(myService.getMetros().size() > 0);
  }

  /**
   * verify static map has no empty or null values
   */
  @Test
  public void verifyMapHasNoNullStatus() {
    Collection<String> copyMap = myService.getMetros().values();
    copyMap.forEach(a -> {
      assertTrue(a != null && !a.equals("") && !a.isEmpty());
    });
  }

  /**
   * verify duplicate keys are not allowed. Otherwise, Map will take the duplicate
   * and replace the old key with the current supplied value.
   */
  @Test
  public void whenDuplicateMetro__thenDuplicateKeyException() {
    assertThrows(Exception.class, () -> {
      myService.addMetro("Chennai");
    });
  }

  /**
   * verify the status of a newly added metro is 'proposed'
   */
  @Test
  public void whenNewMetro__thenVerifyMetrowithStatusProposed() {
    myService.addMetro("Pune");
    assertEquals("Proposed", myService.getMetros().get("Pune"));
  }

}
