package com.andrijatomic.contactmanager.utils;

import com.andrijatomic.contactmanager.dtos.AddContactCsvDTO;
import com.andrijatomic.contactmanager.exceptions.ContactTypeNotFoundException;
import com.andrijatomic.contactmanager.models.Contact;
import com.andrijatomic.contactmanager.repos.ContactTypeRepo;
import com.github.f4b6a3.tsid.TsidCreator;
import java.util.function.Function;
import org.springframework.stereotype.Service;

@Service
public class AddContactCsvDTOMapper implements Function<AddContactCsvDTO, Contact> {

  private final ContactTypeRepo contactTypeRepo;



  public AddContactCsvDTOMapper(ContactTypeRepo contactTypeRepo) {
    this.contactTypeRepo = contactTypeRepo;
  }

  @Override
  public Contact apply(AddContactCsvDTO addContactCsvDTO) {
    return new Contact(
        null,
        TsidCreator.getTsid().toLong(),
        addContactCsvDTO.getFirstName(),
        addContactCsvDTO.getLastName(),
        addContactCsvDTO.getAddress(),
        addContactCsvDTO.getPhoneNumber(),
        null,
        contactTypeRepo.getContactTypeByType(addContactCsvDTO.getContactType())
            .orElseThrow(() -> new ContactTypeNotFoundException(addContactCsvDTO.getContactType()))
    );
  }
}
