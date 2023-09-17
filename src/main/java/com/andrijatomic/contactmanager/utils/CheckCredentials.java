package com.andrijatomic.contactmanager.utils;

import com.andrijatomic.contactmanager.models.AppUser;
import com.andrijatomic.contactmanager.models.Contact;

public class CheckCredentials {

  public static boolean checkIfAppUserOwnsContact(AppUser loggedInUser, Contact contact) {
    return contact.getAppUser().getTsid().equals(loggedInUser.getTsid());
  }

  public static boolean checkIfAdmin(AppUser loggedInUser) {
    return loggedInUser
        .getAuthorities()
        .stream()
        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
  }
}
