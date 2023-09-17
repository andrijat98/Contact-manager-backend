package com.andrijatomic.contactmanager.utils;

import com.andrijatomic.contactmanager.dtos.UpdateContactRequestDTO;
import com.andrijatomic.contactmanager.exceptions.ContactNotFoundException;
import com.andrijatomic.contactmanager.exceptions.ContactTypeNotFoundException;
import com.andrijatomic.contactmanager.models.Contact;
import com.andrijatomic.contactmanager.repos.ContactRepo;
import com.andrijatomic.contactmanager.repos.ContactTypeRepo;
import java.util.function.Function;
import org.springframework.stereotype.Service;

@Service
public class UpdateContactRequestDTOMapper implements Function<UpdateContactRequestDTO, Contact> {

  private final ContactRepo contactRepo;
  private final ContactTypeRepo contactTypeRepo;

  public UpdateContactRequestDTOMapper(ContactRepo contactRepo, ContactTypeRepo contactTypeRepo) {
    this.contactRepo = contactRepo;
    this.contactTypeRepo = contactTypeRepo;
  }

  @Override
  public Contact apply(UpdateContactRequestDTO updateContactRequestDTO) {

    Contact contactToBeUpdated = contactRepo
        .getContactByTsid(Long.parseLong(updateContactRequestDTO.contactTsid()))
        .orElseThrow(() -> new ContactNotFoundException(Long.parseLong(updateContactRequestDTO.contactTsid())));

    contactToBeUpdated.setFirstName(updateContactRequestDTO.firstName());
    contactToBeUpdated.setLastName(updateContactRequestDTO.lastName());
    contactToBeUpdated.setAddress(updateContactRequestDTO.address());
    contactToBeUpdated.setPhoneNumber(updateContactRequestDTO.phoneNumber());
    contactToBeUpdated.setContactType(contactTypeRepo
        .getContactTypeByTsid(Long.parseLong(updateContactRequestDTO.contactTypeTsid()))
        .orElseThrow(
            () -> new ContactTypeNotFoundException(updateContactRequestDTO.contactTypeTsid())));
    return contactToBeUpdated;
  }
}
