package com.andrijatomic.contactmanager.utils;

import com.andrijatomic.contactmanager.dtos.AddContactRequestDTO;
import com.andrijatomic.contactmanager.models.Contact;
import com.github.f4b6a3.tsid.TsidCreator;
import java.util.function.Function;
import org.springframework.stereotype.Service;

@Service
public class AddContactRequestDTOMapper implements Function<AddContactRequestDTO, Contact> {

  @Override
  public Contact apply(AddContactRequestDTO addContactRequestDTO) {
    return new Contact(
        null,
        TsidCreator.getTsid().toLong(),
        addContactRequestDTO.firstName(),
        addContactRequestDTO.lastName(),
        addContactRequestDTO.address(),
        addContactRequestDTO.phoneNumber(),
        null,
        null
    );
  }
}
