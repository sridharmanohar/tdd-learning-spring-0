package com.tdd.spring;

import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class MetroService {

  private MetroRepository metroRepository;

  public MetroService(MetroRepository metroRepository) {
    this.metroRepository = metroRepository;
  }

  public Set<String> getMetros() {
    return metroRepository.findMetros();
  }

}
