package com.tdd.spring;

import java.util.Set;

import org.springframework.stereotype.Repository;

@Repository
public interface MetroRepository extends org.springframework.data.repository.Repository<Metro, Integer>{

  public Set<String> findMetros(); 
}
