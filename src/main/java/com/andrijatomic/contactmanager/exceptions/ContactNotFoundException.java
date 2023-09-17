package com.andrijatomic.contactmanager.exceptions;

public class ContactNotFoundException extends RuntimeException{
  public ContactNotFoundException(Long tsid) {
    super("Contact with TSID " + tsid + " not found.");
  }
}
