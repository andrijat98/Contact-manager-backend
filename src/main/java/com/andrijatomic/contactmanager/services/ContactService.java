package com.andrijatomic.contactmanager.services;

import com.andrijatomic.contactmanager.dtos.AddContactCsvDTO;
import com.andrijatomic.contactmanager.dtos.AddContactRequestDTO;
import com.andrijatomic.contactmanager.dtos.ContactResponseDTO;
import com.andrijatomic.contactmanager.dtos.SearchRequestDTO;
import com.andrijatomic.contactmanager.dtos.UpdateContactRequestDTO;
import com.andrijatomic.contactmanager.exceptions.AppUserNotFoundException;
import com.andrijatomic.contactmanager.exceptions.ContactNotFoundException;
import com.andrijatomic.contactmanager.exceptions.ContactTypeNotFoundException;
import com.andrijatomic.contactmanager.models.AppUser;
import com.andrijatomic.contactmanager.models.Contact;
import com.andrijatomic.contactmanager.repos.AppUserRepo;
import com.andrijatomic.contactmanager.repos.ContactRepo;
import com.andrijatomic.contactmanager.repos.ContactTypeRepo;
import com.andrijatomic.contactmanager.utils.AddContactCsvDTOMapper;
import com.andrijatomic.contactmanager.utils.AddContactRequestDTOMapper;
import com.andrijatomic.contactmanager.utils.CheckCredentials;
import com.andrijatomic.contactmanager.utils.ContactResponseDTOMapper;
import com.andrijatomic.contactmanager.utils.CountUtil;
import com.andrijatomic.contactmanager.utils.FieldExtractor;
import com.andrijatomic.contactmanager.utils.UpdateContactRequestDTOMapper;
import com.github.f4b6a3.tsid.TsidCreator;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ContactService {

  private final ContactRepo contactRepo;
  private final ContactResponseDTOMapper contactResponseDTOMapper;
  private final AppUserRepo appUserRepo;
  private final ContactTypeRepo contactTypeRepo;
  private final AddContactRequestDTOMapper addContactRequestDTOMapper;
  private final UpdateContactRequestDTOMapper updateContactRequestDTOMapper;
  private final AddContactCsvDTOMapper addContactCsvDTOMapper;

  public ContactService(ContactRepo contactRepo, ContactResponseDTOMapper contactResponseDTOMapper,
      AppUserRepo appUserRepo, ContactTypeRepo contactTypeRepo,
      AddContactRequestDTOMapper addContactRequestDTOMapper,
      UpdateContactRequestDTOMapper updateContactRequestDTOMapper,
      AddContactCsvDTOMapper addContactCsvDTOMapper) {
    this.contactRepo = contactRepo;
    this.contactResponseDTOMapper = contactResponseDTOMapper;
    this.appUserRepo = appUserRepo;
    this.contactTypeRepo = contactTypeRepo;
    this.addContactRequestDTOMapper = addContactRequestDTOMapper;
    this.updateContactRequestDTOMapper = updateContactRequestDTOMapper;
    this.addContactCsvDTOMapper = addContactCsvDTOMapper;
  }

  public ResponseEntity<?> getContact(Long tsid, AppUser loggedInUser) {

    Contact retrievedContact = contactRepo.getContactByTsid(tsid)
        .orElseThrow(() -> new ContactNotFoundException(tsid));

    if (CheckCredentials.checkIfAppUserOwnsContact(loggedInUser, retrievedContact)) {
      return new ResponseEntity<>(contactResponseDTOMapper.apply(retrievedContact), HttpStatus.OK);
    }
    return new ResponseEntity<>("Contact not found", HttpStatus.NOT_FOUND);
  }


  public List<ContactResponseDTO> getAllContacts(int page, int size, String sortByProperty,
      AppUser loggedInUser) {

    return contactRepo
        .getContactsByAppUser(loggedInUser, PageRequest.of(page, size, Sort.by(sortByProperty)))
        .stream()
        .map(contactResponseDTOMapper)
        .collect(Collectors.toList());
  }

  public List<ContactResponseDTO> searchContacts(SearchRequestDTO searchRequest,
      AppUser loggedInUser) {

    List<Contact> retrievedContacts = switch (searchRequest.searchParameter()) {
      case "firstName" -> contactRepo.getContactByAppUserAndFirstNameContainingIgnoreCase(
          loggedInUser, searchRequest.searchKeyword());
      case "lastName" -> contactRepo.getContactByAppUserAndLastNameContainingIgnoreCase(
          loggedInUser, searchRequest.searchKeyword());
      case "address" -> contactRepo.getContactByAppUserAndAddressContainingIgnoreCase(
          loggedInUser, searchRequest.searchKeyword());
      case "phoneNumber" -> contactRepo.getContactByAppUserAndPhoneNumberContainingIgnoreCase(
          loggedInUser, searchRequest.searchKeyword());
      default -> new ArrayList<>();
    };

    @SuppressWarnings("unchecked, rawtypes")
    Comparator<Contact> comparator = Comparator.<Contact, Comparable>comparing(
        FieldExtractor.getFieldExtractor(searchRequest.sortBy())
    );

    return retrievedContacts
        .stream()
        .sorted(comparator)
        .skip((long) searchRequest.size() * searchRequest.page())
        .limit(searchRequest.size())
        .map(contactResponseDTOMapper)
        .collect(Collectors.toList());
  }

  public ContactResponseDTO addContact(AddContactRequestDTO addContactRequestDTO,
      AppUser loggedInUser) {

    Contact contactToBeAdded = addContactRequestDTOMapper.apply(addContactRequestDTO);
    contactToBeAdded.setTsid(TsidCreator.getTsid().toLong());

    contactToBeAdded.setAppUser(appUserRepo.getAppUserByTsid(
        loggedInUser.getTsid()).orElseThrow(
            () -> new AppUserNotFoundException("TSID", loggedInUser.getTsid().toString())
    ));

    contactToBeAdded.setContactType(contactTypeRepo.getContactTypeByTsid(
        Long.parseLong(addContactRequestDTO.contactTypeTsid())).orElseThrow(
        () -> new ContactTypeNotFoundException(addContactRequestDTO.contactTypeTsid())
    ));

    contactRepo.save(contactToBeAdded);

    return contactResponseDTOMapper.apply(contactToBeAdded);
  }

  public ResponseEntity<?> updateContact(UpdateContactRequestDTO contactRequestDTO, AppUser loggedInUser) {

    Contact contactToBeUpdated = updateContactRequestDTOMapper.apply(contactRequestDTO);

    if (CheckCredentials.checkIfAppUserOwnsContact(loggedInUser, contactToBeUpdated)) {

      return new ResponseEntity<>(contactResponseDTOMapper
          .apply(contactRepo.save(contactToBeUpdated)),
          HttpStatus.OK);
    }
    return new ResponseEntity<>("Contact not found", HttpStatus.NOT_FOUND);
  }

  public ResponseEntity<?> deleteContact(Long tsid, AppUser loggedInUser) {

    Contact contactToBeDeleted = contactRepo.getContactByTsid(tsid)
        .orElseThrow(() -> new ContactNotFoundException(tsid));

    if (CheckCredentials.checkIfAppUserOwnsContact(loggedInUser, contactToBeDeleted)) {

      contactRepo.deleteContactByTsid(tsid);
      return new ResponseEntity<>(HttpStatus.OK);
    }

    return ResponseEntity.notFound().build();
  }
  public void exportContactsToCsv(AppUser loggedInUser, HttpServletResponse response)
      throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {

    String fileName = "contacts.csv";

    List<Contact> contacts = contactRepo
        .getContactsByAppUser(loggedInUser, Sort.by(Direction.ASC, "firstName"));

    List<ContactResponseDTO> contactResponse = contacts.stream().map(contactResponseDTOMapper)
        .toList();

    response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=\"" + fileName + "\"");
    response.setContentType("text/csv");

    StatefulBeanToCsv<ContactResponseDTO> csvWriter =
        new StatefulBeanToCsvBuilder<ContactResponseDTO>(response.getWriter())
        .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
        .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
        .withOrderedResults(true)
        .build();

    csvWriter.write(contactResponse);
  }

  public ResponseEntity<?> importContactsFromCsvFile(@RequestPart MultipartFile file,
      AppUser loggedInUser) {

    String fileName = file.getOriginalFilename();

    Optional<String> fileExtension = Optional.ofNullable(fileName)
        .filter(f -> f.contains("."))
        .map(f -> f.substring(fileName.lastIndexOf(".") + 1));
    if(fileExtension.isPresent() && !fileExtension.get().equals("csv")) {
      return new ResponseEntity<>("File type is not .csv", HttpStatus.BAD_REQUEST);
    }

    if(file.isEmpty()) {
      return new ResponseEntity<>("File is empty", HttpStatus.BAD_REQUEST);
    }

    try(Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

      CsvToBean<AddContactCsvDTO> csvToBean = new CsvToBeanBuilder<AddContactCsvDTO>(reader)
          .withType(AddContactCsvDTO.class)
          .withIgnoreLeadingWhiteSpace(true)
          .build();

      List<AddContactCsvDTO> contactsFromCsv = csvToBean.parse();

      ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
      Validator validator = validatorFactory.getValidator();

      AtomicInteger counter = new AtomicInteger(0);

      contactsFromCsv.stream()
          .filter(addContactCsvDTO -> validator.validate(addContactCsvDTO).isEmpty())
          .map(addContactCsvDTOMapper)
          .forEach(contact -> {
            counter.getAndIncrement();
            contact.setAppUser(loggedInUser);
            contactRepo.save(contact);
          });

      int errored = contactsFromCsv.size() - counter.get();

      return new ResponseEntity<>(counter + " contact(s) added. " + errored + " errored.",
          HttpStatus.OK);

    } catch (IOException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  public ResponseEntity<?> countAllContacts() {
    return CountUtil.count(contactRepo);
  }
}
