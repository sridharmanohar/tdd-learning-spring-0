package com.tdd.spring;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface MetroRepository
    extends org.springframework.data.repository.Repository<Metro, Integer> {

  public List<Metro> findAll();
}
