package com.andrijatomic.contactmanager.repos;

import com.andrijatomic.contactmanager.models.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepo extends JpaRepository<AppUser, Long> {

  Optional<AppUser> getAppUserByTsid(Long tsid);
  Optional<AppUser> findByEmail(String email);
  void deleteAppUserByTsid(Long tsid);

}
