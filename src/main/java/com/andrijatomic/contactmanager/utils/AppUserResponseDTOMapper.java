package com.andrijatomic.contactmanager.utils;

import com.andrijatomic.contactmanager.dtos.AppUserResponseDTO;
import com.andrijatomic.contactmanager.dtos.AppUserRoleResponseDTO;
import com.andrijatomic.contactmanager.models.AppUser;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AppUserResponseDTOMapper implements Function<AppUser, AppUserResponseDTO> {

  @Override
  public AppUserResponseDTO apply(AppUser appUser) {

    return new AppUserResponseDTO(
        appUser.getTsid().toString(),
        appUser.getFirstName(),
        appUser.getLastName(),
        appUser.getEmail(),
        appUser.getPhoneNumber(),
        appUser.isPhoneVerified(),
        appUser
            .getRoles()
            .stream()
            .map(role -> new AppUserRoleResponseDTO(role.getTsid().toString(),
            role.getRoleName()))
            .collect(Collectors.toList())
    );
  }
}
