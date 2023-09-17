package com.andrijatomic.contactmanager.exceptions;

public class ContactTypeNotFoundException extends RuntimeException{

  public ContactTypeNotFoundException(Long contactTypeId) {
    super("Contact type with TSID " + contactTypeId + " not found.");
  }
  public ContactTypeNotFoundException(String contactTypeName) {
    super("Contact type with name " + contactTypeName + " not found.");
  }
}
