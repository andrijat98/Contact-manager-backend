package com.andrijatomic.contactmanager.repos;

import com.andrijatomic.contactmanager.models.AppUser;
import com.andrijatomic.contactmanager.models.Contact;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContactRepo extends JpaRepository<Contact, Long>{

  Optional<Contact> getContactByTsid(Long tsid);
  List<Contact> getContactsByAppUser(AppUser appUser, Pageable pageable);
  List<Contact> getContactsByAppUser(AppUser appUser, Sort sort);
  List<Contact> getContactByAppUserAndFirstNameContainingIgnoreCase(AppUser appUser, String firstName);
  List<Contact> getContactByAppUserAndLastNameContainingIgnoreCase(AppUser appUser, String lastName);
  List<Contact> getContactByAppUserAndAddressContainingIgnoreCase(AppUser appUser, String address);
  List<Contact> getContactByAppUserAndPhoneNumberContainingIgnoreCase(AppUser appUser, String phoneNumber);
  @Modifying
  @Query("DELETE FROM Contact c WHERE c.tsid = :contactTsid")
  void deleteContactByTsid(@Param("contactTsid") Long contactTsid);
}
