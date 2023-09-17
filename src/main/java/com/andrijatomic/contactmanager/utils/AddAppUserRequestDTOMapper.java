package com.andrijatomic.contactmanager.utils;

import com.andrijatomic.contactmanager.dtos.AddAppUserRequestDTO;
import com.andrijatomic.contactmanager.models.AppUser;
import com.github.f4b6a3.tsid.TsidCreator;
import java.util.ArrayList;
import java.util.function.Function;
import org.springframework.stereotype.Service;

@Service
public class AddAppUserRequestDTOMapper implements Function<AddAppUserRequestDTO, AppUser> {

  @Override
  public AppUser apply(AddAppUserRequestDTO addAppUserRequestDTO) {
    return new AppUser(
        null,
        TsidCreator.getTsid().toLong(),
        addAppUserRequestDTO.firstName(),
        addAppUserRequestDTO.lastName(),
        addAppUserRequestDTO.email(),
        addAppUserRequestDTO.password(),
        addAppUserRequestDTO.phoneNumber(),
        false,
        false,
        new ArrayList<>(),
        new ArrayList<>()
    );
  }
}
