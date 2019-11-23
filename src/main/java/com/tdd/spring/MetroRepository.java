package com.tdd.spring;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MetroRepository
    extends org.springframework.data.repository.Repository<Metro, Integer> {

  List<Metro> findAll();

  Metro findByName(String name);

  @Transactional
  void save(Metro metro);
}
