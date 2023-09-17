package com.andrijatomic.contactmanager.repos;

import com.andrijatomic.contactmanager.models.ContactType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactTypeRepo extends JpaRepository<ContactType, Long> {
  Optional<ContactType> getContactTypeByTsid(Long tsid);
  Optional<ContactType> getContactTypeByType(String type);
}
