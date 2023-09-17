package com.andrijatomic.contactmanager.repos;

import com.andrijatomic.contactmanager.models.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, Long> {
  Optional<Role> getRoleByTsid(Long tsid);
}
