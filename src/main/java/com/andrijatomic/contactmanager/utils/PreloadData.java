package com.andrijatomic.contactmanager.utils;

import com.andrijatomic.contactmanager.models.ContactType;
import com.andrijatomic.contactmanager.models.Role;
import com.andrijatomic.contactmanager.repos.ContactTypeRepo;
import com.andrijatomic.contactmanager.repos.RoleRepo;
import com.github.f4b6a3.tsid.TsidCreator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class PreloadData {

  @Bean
  @ConditionalOnProperty(value = "contactmanager.preload-data",
      havingValue = "true")
  public CommandLineRunner dataLoader(RoleRepo roleRepo, ContactTypeRepo contactTypeRepo) {
    return args -> {
      roleRepo.save(new Role(
          null, TsidCreator.getTsid().toLong(), "ROLE_USER", null));
      roleRepo.save(new Role(
          null, TsidCreator.getTsid().toLong(), "ROLE_ADMIN", null));
      contactTypeRepo.save(new ContactType(
          null, TsidCreator.getTsid().toLong(), "Family", null));
      contactTypeRepo.save(new ContactType(
          null, TsidCreator.getTsid().toLong(), "Home", null));
      contactTypeRepo.save(new ContactType(
          null, TsidCreator.getTsid().toLong(), "Friend", null));
      contactTypeRepo.save(new ContactType(
          null, TsidCreator.getTsid().toLong(), "Work", null));
    };
  }
}
