package com.andrijatomic.contactmanager.exceptions;

public class AppUserNotFoundException extends RuntimeException {

  public AppUserNotFoundException(String fieldName, String fieldValue) {
    super("User with " + fieldName + " " + fieldValue + " not found.");
  }
}
