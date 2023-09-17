package com.andrijatomic.contactmanager.services;

import com.andrijatomic.contactmanager.dtos.AddContactTypeRequestDTO;
import com.andrijatomic.contactmanager.dtos.ContactTypeResponseDTO;
import com.andrijatomic.contactmanager.dtos.UpdateContactTypeRequestDTO;
import com.andrijatomic.contactmanager.exceptions.ContactTypeNotFoundException;
import com.andrijatomic.contactmanager.models.ContactType;
import com.andrijatomic.contactmanager.repos.ContactTypeRepo;
import com.github.f4b6a3.tsid.TsidCreator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ContactTypeService {

  private final ContactTypeRepo contactTypeRepo;

  public ContactTypeService(ContactTypeRepo contactTypeRepo) {
    this.contactTypeRepo = contactTypeRepo;
  }

  public ResponseEntity<?> getAllContactTypes() {

    List<ContactType> allContactTypes = contactTypeRepo.findAll();

    if (allContactTypes.isEmpty()) {
      return new ResponseEntity<>("No contact types found", HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(contactTypeRepo
        .findAll()
        .stream()
        .map(type -> new ContactTypeResponseDTO(type.getTsid().toString(), type.getType()))
        .collect(Collectors.toList()),
        HttpStatus.OK);
  }

  public ResponseEntity<ContactTypeResponseDTO> addContactType(AddContactTypeRequestDTO requestDTO) {

    ContactType contactType = new ContactType(
        null, TsidCreator.getTsid().toLong(), requestDTO.type(), null
    );

    ContactType savedContactType = contactTypeRepo.save(contactType);

    return new ResponseEntity<>(new ContactTypeResponseDTO(
        savedContactType.getTsid().toString(), savedContactType.getType()), HttpStatus.OK
    );
  }

  public ResponseEntity<ContactTypeResponseDTO> updateContactType(UpdateContactTypeRequestDTO requestDTO) {

    ContactType retrievedType = contactTypeRepo
        .getContactTypeByTsid(requestDTO.tsid())
        .orElseThrow(() -> new ContactTypeNotFoundException(requestDTO.tsid()));

    retrievedType.setType(requestDTO.type());

    ContactType savedContactType = contactTypeRepo.save(retrievedType);

    return new ResponseEntity<>(new ContactTypeResponseDTO(
        savedContactType.getTsid().toString(), savedContactType.getType()), HttpStatus.OK
    );
  }
}
