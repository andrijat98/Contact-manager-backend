package com.andrijatomic.contactmanager.exceptions;

public class RoleNotFoundException extends RuntimeException{
  public RoleNotFoundException(Long tsid) {
    super("Role with TSID " + tsid + " not found.");
  }
}
