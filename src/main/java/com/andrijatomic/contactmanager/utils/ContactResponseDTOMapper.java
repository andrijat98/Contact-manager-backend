package com.andrijatomic.contactmanager.utils;

import com.andrijatomic.contactmanager.dtos.ContactResponseDTO;
import com.andrijatomic.contactmanager.models.Contact;
import java.util.function.Function;
import org.springframework.stereotype.Service;

@Service
public class ContactResponseDTOMapper implements Function<Contact, ContactResponseDTO> {

  @Override
  public ContactResponseDTO apply(Contact contact) {
    return new ContactResponseDTO(
        contact.getTsid().toString(),
        contact.getFirstName(),
        contact.getLastName(),
        contact.getAddress(),
        contact.getPhoneNumber(),
        contact.getContactType().getType(),
        contact.getContactType().getTsid().toString()
    );
  }
}
