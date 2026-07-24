package com.mycompany.techstore.Exceptions;

public class BackOfficeNotFoundException extends RuntimeException {

  public BackOfficeNotFoundException(String message) {
    super(message);
  }
}
