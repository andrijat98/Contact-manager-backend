package com.andrijatomic.contactmanager.utils;

import com.andrijatomic.contactmanager.dtos.UpdateAppUserRequestDTO;
import com.andrijatomic.contactmanager.exceptions.AppUserNotFoundException;
import com.andrijatomic.contactmanager.models.AppUser;
import com.andrijatomic.contactmanager.repos.AppUserRepo;
import com.andrijatomic.contactmanager.repos.RoleRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UpdateAppUserRequestDTOMapper {

  private final AppUserRepo userRepo;
  private final PasswordEncoder encoder;
  private final RoleRepo roleRepo;

  public UpdateAppUserRequestDTOMapper(AppUserRepo userRepo, PasswordEncoder encoder,
      RoleRepo roleRepo) {
    this.userRepo = userRepo;
    this.encoder = encoder;
    this.roleRepo = roleRepo;
  }

  public AppUser map(UpdateAppUserRequestDTO requestDTO, AppUser loggedInUser) {

    AppUser userToBeUpdated;

    if (CheckCredentials.checkIfAdmin(loggedInUser) && requestDTO.tsid() != null) {
      userToBeUpdated = userRepo
          .getAppUserByTsid(Long.parseLong(requestDTO.tsid()))
          .orElseThrow(() -> new AppUserNotFoundException("tsid", requestDTO.tsid()));

      String email = requestDTO.email();
      if (email != null) {
        if (!email.isBlank()) {
          userToBeUpdated.setEmail(email);
          userToBeUpdated.setEnabled(false);
        }
      }

      userToBeUpdated.setRoles(RoleTsidToRoleConverterUtil
          .convertToRoles(requestDTO.userRoleTsids(), roleRepo));

    } else {
      userToBeUpdated = loggedInUser;
    }
    return setInitialValues(userToBeUpdated, requestDTO);
  }

  private AppUser setInitialValues(AppUser appUserToBeUpdated, UpdateAppUserRequestDTO requestDTO) {

    String firstName = requestDTO.firstName();
    String lastName = requestDTO.lastName();
    String phoneNumber = requestDTO.phoneNumber();
    String password = requestDTO.password();

    if (firstName != null) {
      if (!firstName.isBlank()) {
        appUserToBeUpdated.setFirstName(firstName);
      }
    }

    if (lastName != null) {
      if (!lastName.isBlank()) {
        appUserToBeUpdated.setLastName(lastName);
      }
    }

    if (phoneNumber != null) {
      if (!phoneNumber.isBlank()) {
        appUserToBeUpdated.setPhoneNumber(phoneNumber);
        appUserToBeUpdated.setPhoneVerified(false);
      }
    }

    if (password != null) {
      if (!password.isBlank()) {
        appUserToBeUpdated.setPassword(encoder.encode(password));
      }
    }

    return appUserToBeUpdated;

  }
}
