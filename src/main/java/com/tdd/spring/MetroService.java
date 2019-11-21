package com.tdd.spring;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class.
 * 
 * @author sridhar
 *
 */
@Service
public class MetroService {

  private final MetroRepository metroRepository;

  @Autowired
  public MetroService(MetroRepository metroRepository) {
    this.metroRepository = metroRepository;
  }


  /**
   * Returns Metros (avoiding removing any duplicates).
   */
  public Set<String> getMetros() {
    List<Metro> metroList = metroRepository.findAll();
    Set<String> metroSet = new HashSet<>();
    metroList.forEach(list -> metroSet.add(list.getName()));
    return metroSet;
  }

}
