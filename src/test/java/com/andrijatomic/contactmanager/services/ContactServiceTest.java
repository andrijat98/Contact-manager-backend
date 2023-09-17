package com.andrijatomic.contactmanager.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andrijatomic.contactmanager.dtos.AddContactRequestDTO;
import com.andrijatomic.contactmanager.dtos.ContactResponseDTO;
import com.andrijatomic.contactmanager.dtos.SearchRequestDTO;
import com.andrijatomic.contactmanager.exceptions.ContactNotFoundException;
import com.andrijatomic.contactmanager.models.AppUser;
import com.andrijatomic.contactmanager.models.Contact;
import com.andrijatomic.contactmanager.models.ContactType;
import com.andrijatomic.contactmanager.repos.AppUserRepo;
import com.andrijatomic.contactmanager.repos.ContactRepo;
import com.andrijatomic.contactmanager.repos.ContactTypeRepo;
import com.andrijatomic.contactmanager.utils.AddContactCsvDTOMapper;
import com.andrijatomic.contactmanager.utils.AddContactRequestDTOMapper;
import com.andrijatomic.contactmanager.utils.ContactResponseDTOMapper;
import com.andrijatomic.contactmanager.utils.UpdateContactRequestDTOMapper;
import com.github.f4b6a3.tsid.TsidCreator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {
  /*
  private Role userRole;
  private Role adminRole;
  private ContactType homeContactType;
  private ContactType friendContactType;
  private ContactType workContactType;
   */
  private ContactType familyContactType;
  @Mock
  private ContactRepo contactRepo;
  @Mock
  private AppUserRepo appUserRepo;
  @Mock
  private ContactTypeRepo contactTypeRepo;
  @Mock
  private ContactResponseDTOMapper responseMapper;
  @Mock
  private AddContactRequestDTOMapper addRequestMapper;
  @Mock
  private UpdateContactRequestDTOMapper updateRequestMapper;
  @Mock
  private AddContactCsvDTOMapper addCsvMapper;
  @Mock
  PasswordEncoder encoder;

  private ContactService underTest;

  private final Long tsid = 1L;
  private final AppUser loggedInUser = new AppUser();
  @BeforeEach
  void setUp() {
    underTest = new ContactService(
        contactRepo, responseMapper, appUserRepo, contactTypeRepo, addRequestMapper,
        updateRequestMapper, addCsvMapper);
    encoder = new BCryptPasswordEncoder();
    //userRole = new Role(1L, TsidCreator.getTsid().toLong(), "ROLE_USER", null);
    //adminRole = new Role(2L, TsidCreator.getTsid().toLong(), "ROLE_ADMIN", null);
    familyContactType = new ContactType(1L, TsidCreator.getTsid().toLong(), "Family", null);
    //homeContactType = new ContactType(1L, TsidCreator.getTsid().toLong(), "Home", null);
    //friendContactType = new ContactType(1L, TsidCreator.getTsid().toLong(), "Friend", null);
    //workContactType = new ContactType(1L, TsidCreator.getTsid().toLong(), "Work", null);
  }

  @Test
  void canGetContactWhenContactFound() {

    // given
    Contact retrievedContact = new Contact(1L, 1L, "FirstName", "LastName", "Some address 11",
        "+381658474848", null, null);
    retrievedContact.setContactType(familyContactType);

    AppUser loggedInUser = new AppUser(1L, 1L, "John", "Doe", "johndoe@gmail.com",
        encoder.encode("password"), "+381658478598", true, true, null, null);

    loggedInUser.setContacts(List.of(retrievedContact));
    retrievedContact.setAppUser(loggedInUser);

    when(contactRepo.getContactByTsid(tsid)).thenReturn(Optional.of(retrievedContact));
    when(responseMapper.apply(retrievedContact)).thenReturn(new ContactResponseDTO(
        "1", "FirstName", "LastName", "Some Address 11", "+381658474848", "Home", "1"
    ));
    loggedInUser.setContacts(List.of(retrievedContact));

    // when
    ResponseEntity<?> response = underTest.getContact(tsid, loggedInUser);

    // then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(responseMapper).apply(retrievedContact);
  }

  @Test
  void canThrowExceptionWhenContactNotFound() {

    // given
    // when
    ContactNotFoundException exception = assertThrows(ContactNotFoundException.class, () ->
        underTest.getContact(tsid, loggedInUser));

    // then
    assertEquals("Contact with TSID 1 not found.", exception.getMessage());
  }

  @Test
  void getAllContacts() {
    // given
    int pageNum = 0;
    int pageSize = 10;
    String sortByProperty = "firstName";
    //when
    underTest.getAllContacts(pageNum, pageSize, sortByProperty, new AppUser());
    // then
    verify(contactRepo).getContactsByAppUser(new AppUser(), PageRequest.of(pageNum, pageSize, Sort.by(sortByProperty)));
  }

  @Test
  void searchContacts() {

    // given
    SearchRequestDTO searchRequest = new SearchRequestDTO(
        "firstName", "John", 0, 10, "firstName"
    );

    AppUser loggedInUser = new AppUser(1L, 1L, "John", "Doe", "johndoe@gmail.com",
        encoder.encode("password"), "+381658478598", true, true, null, null);

    List<Contact> mockedContacts = List.of(
        new Contact(1L, tsid, "John", "Doe", "Some Address 1", "+381654987878", null, null),
        new Contact(2L, 2L, "John", "Other", "Some Address 2", "+381654987879", null, null)
    );
    loggedInUser.setContacts(mockedContacts);

    //when
    when(contactRepo.getContactByAppUserAndFirstNameContainingIgnoreCase(loggedInUser, "John"))
        .thenReturn(mockedContacts);
    when(responseMapper.apply(any(Contact.class))).thenReturn(new ContactResponseDTO(
        "1L", "John", "Doe", "Some Address 1", "+381638457984", "Work", "1L"
    ));

    List<ContactResponseDTO> result = underTest.searchContacts(searchRequest, loggedInUser);

    //then
    verify(contactRepo).getContactByAppUserAndFirstNameContainingIgnoreCase(loggedInUser, "John");
    verify(responseMapper, times(2)).apply(any(Contact.class));
    assertEquals(2, result.size());

  }
  @Test
  void canAddContact() {

    // given
    AddContactRequestDTO addContact = new AddContactRequestDTO(
        "John", "Doe", "Some address 1", "+381626547945", "1"
    );

    AppUser loggedInUser = new AppUser(1L, 1L, "John", "Doe", "johndoe@gmail.com",
        encoder.encode("password"), "+381658478598", true, true, null, null);

    //when
    Contact returnedContact = new Contact(
        1L, tsid, "John", "Doe", "Some Address 1", "+381654987878", null, null
    );

    when(addRequestMapper.apply(addContact)).thenReturn(returnedContact);
    when(appUserRepo.getAppUserByTsid(tsid)).thenReturn(Optional.of(loggedInUser));
    when(contactTypeRepo.getContactTypeByTsid(tsid)).thenReturn(
        Optional.ofNullable(familyContactType));
    underTest.addContact(addContact, loggedInUser);

    // then
    ArgumentCaptor<Contact> contactArgumentCaptor = ArgumentCaptor.forClass(Contact.class);
    verify(contactRepo).save(contactArgumentCaptor.capture());

    Contact capturedContact = contactArgumentCaptor.getValue();
    assertThat(capturedContact).isEqualTo(returnedContact);

  }
  @Disabled
  @Test
  void updateContact() {
  }
  @Disabled
  @Test
  void deleteContact() {
  }
  @Disabled
  @Test
  void exportContactsToCsv() {
  }
  @Disabled
  @Test
  void importContactsFromCsvFile() {
  }
  @Disabled
  @Test
  void getAllContactTypes() {
  }
}