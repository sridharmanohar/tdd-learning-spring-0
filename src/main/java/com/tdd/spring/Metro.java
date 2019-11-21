package com.tdd.spring;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "metros")
public class Metro {

  @Id
  private Integer id;

  private String name;

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
