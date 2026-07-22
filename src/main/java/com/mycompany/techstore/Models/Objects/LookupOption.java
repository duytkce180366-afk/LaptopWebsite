package com.mycompany.techstore.Models.Objects;

public class LookupOption {
  private final int id;
  private final String name;

  public LookupOption(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}
