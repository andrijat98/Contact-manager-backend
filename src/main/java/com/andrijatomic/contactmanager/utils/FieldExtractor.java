package com.andrijatomic.contactmanager.utils;

import com.andrijatomic.contactmanager.models.Contact;
import java.util.function.Function;

public class FieldExtractor {
  public static Function<Contact, Comparable<?>> getFieldExtractor(String fieldName) {
    return switch (fieldName) {
      case "lastName" -> Contact::getLastName;
      case "address" -> Contact::getAddress;
      case "phoneNumber" -> Contact::getPhoneNumber;
      default -> Contact::getFirstName;
    };
  }
}
