package com.tdd.spring;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "metros")
public class Metro {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "metro_id_seq")
  @SequenceGenerator(name = "metro_id_seq", sequenceName = "metro_id_seq", allocationSize = 1)
  private Integer id;

  private String name;

  private String status;
  
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Metro() {
    super();
  }

  public Metro(String string) {
    this.name = string;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
