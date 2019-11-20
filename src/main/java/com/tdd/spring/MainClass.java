package com.tdd.spring;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
public class MainClass {

  private MetroController metroController;
  private MetroService metroService;
  private MetroRepository metroRepository;
  
  public static void main(String[] args) {
//    SpringApplication.run(MainClass.class, args);
    new MainClass().injectDependencies();
  }

  private void injectDependencies() {
    // TODO Auto-generated method stub
    System.out.println(" ------------------ HERE ------------------------------");
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
  
}
