package com.tdd.spring;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

  public Set<Metro> performMetroSubmission(String proposedMetro) {
     Set<Metro> returnSet = new HashSet<Metro>();
    if (checkMetroStatus(proposedMetro) != null) {
      returnSet.add(checkMetroStatus(proposedMetro));
      return returnSet; 
    }
    else {
      return save(callSave(proposedMetro));
    }
  }

  private Metro callSave(String proposedMetro) {
    Metro metro = new Metro();
    metro.setStatus("proposed");
    metro.setName(proposedMetro);
    return metro;
  }
  
  private Set<Metro> save(Metro metro) {
    this.metroRepository.save(metro);
    return this.metroRepository.findAll().stream().collect(Collectors.toSet());
  }

  private Metro checkMetroStatus(String metro) {
    return this.metroRepository.findByName(metro);
  }

}
