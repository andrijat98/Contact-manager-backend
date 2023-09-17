package com.andrijatomic.contactmanager.services;

import com.andrijatomic.contactmanager.dtos.AddAppUserRequestDTO;
import com.andrijatomic.contactmanager.dtos.AppUserResponseDTO;
import com.andrijatomic.contactmanager.dtos.AppUserRoleResponseDTO;
import com.andrijatomic.contactmanager.dtos.UpdateAppUserRequestDTO;
import com.andrijatomic.contactmanager.exceptions.AppUserNotFoundException;
import com.andrijatomic.contactmanager.models.AccountVerification;
import com.andrijatomic.contactmanager.models.AppUser;
import com.andrijatomic.contactmanager.repos.AppUserRepo;
import com.andrijatomic.contactmanager.repos.RoleRepo;
import com.andrijatomic.contactmanager.utils.AddAppUserRequestDTOMapper;
import com.andrijatomic.contactmanager.utils.AppUserResponseDTOMapper;
import com.andrijatomic.contactmanager.utils.CountUtil;
import com.andrijatomic.contactmanager.utils.RoleTsidToRoleConverterUtil;
import com.andrijatomic.contactmanager.utils.UpdateAppUserRequestDTOMapper;
import com.github.f4b6a3.tsid.TsidCreator;
import jakarta.mail.MessagingException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AppUserService {
  private final AppUserRepo appUserRepo;
  private final AppUserResponseDTOMapper appUserResponseDTOMapper;
  private final AddAppUserRequestDTOMapper addAppUserRequestDTOMapper;

  private final UpdateAppUserRequestDTOMapper updateRequestDTOMapper;
  private final RoleRepo roleRepo;
  private final AccountVerificationService verificationService;
  private final EmailService emailService;
  private final PasswordEncoder encoder;

  public AppUserService(AppUserRepo appUserRepo, AppUserResponseDTOMapper appUserResponseDTOMapper,
      AddAppUserRequestDTOMapper addAppUserRequestDTOMapper,
      UpdateAppUserRequestDTOMapper updateAppUserRequestDTOMapper, RoleRepo roleRepo,
      AccountVerificationService verificationService, EmailService emailService,
      PasswordEncoder encoder) {
    this.appUserRepo = appUserRepo;
    this.appUserResponseDTOMapper = appUserResponseDTOMapper;
    this.addAppUserRequestDTOMapper = addAppUserRequestDTOMapper;
    this.updateRequestDTOMapper = updateAppUserRequestDTOMapper;
    this.roleRepo = roleRepo;
    this.verificationService = verificationService;
    this.emailService = emailService;
    this.encoder = encoder;
  }

  public AppUserResponseDTO getAppUser(Long tsid) {
    return appUserRepo.getAppUserByTsid(tsid)
        .map(appUserResponseDTOMapper)
        .orElseThrow(() -> new AppUserNotFoundException("TSID", tsid.toString()));
  }

  public List<AppUserResponseDTO> getAllAppUsers(int page, int size, String sortByProperty) {
    return appUserRepo.findAll(PageRequest.of(page, size, Sort.by(sortByProperty)))
        .stream()
        .map(appUserResponseDTOMapper)
        .collect(Collectors.toList());
  }

  public ResponseEntity<?> addAppUser(AddAppUserRequestDTO addAppUserRequestDTO)
      throws MessagingException {

    AppUser appUser = addAppUserRequestDTOMapper.apply(addAppUserRequestDTO);

    appUser.setTsid(TsidCreator.getTsid().toLong());
    appUser.setPassword(encoder.encode(appUser.getPassword()));

    appUser.setRoles(RoleTsidToRoleConverterUtil
        .convertToRoles(addAppUserRequestDTO.userRoleTsids(), roleRepo)
    );

    AppUser addedUser = appUserRepo.save(appUser);
    AccountVerification verification = verificationService.createVerifyLink(addedUser);

    emailService.sendMail(addedUser, verification);

    AppUserResponseDTO responseDTO = appUserRepo.getAppUserByTsid(addedUser.getTsid())
        .map(appUserResponseDTOMapper)
        .orElseThrow(() -> new AppUserNotFoundException("TSID", addedUser.getTsid().toString()));

    return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
  }

  public AppUserResponseDTO updateAppUser(
      UpdateAppUserRequestDTO updateAppUserRequestDTO, AppUser loggedInUser) {

    AppUser updatedUser = appUserRepo
        .save(updateRequestDTOMapper.map(updateAppUserRequestDTO, loggedInUser));

    return appUserRepo.getAppUserByTsid(updatedUser.getTsid())
        .map(appUserResponseDTOMapper)
        .orElseThrow(() -> new AppUserNotFoundException("TSID", updatedUser.getTsid().toString()));
  }

  public void deleteAppUser(Long tsid) {

    if (appUserRepo.getAppUserByTsid(tsid).isEmpty()) {
      throw new AppUserNotFoundException("TSID", tsid.toString());
    } else {
      appUserRepo.deleteAppUserByTsid(tsid);
    }

  }

  public ResponseEntity<?> getAllRoles() {

    List<AppUserRoleResponseDTO> roles = roleRepo
        .findAll()
        .stream()
        .map(role -> new AppUserRoleResponseDTO(role.getTsid().toString(), role.getRoleName()))
        .collect(Collectors.toList());

    if(roles.isEmpty()) {
      return new ResponseEntity<>("No roles found", HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(roles, HttpStatus.OK);
  }

  public ResponseEntity<?> countAllAppUsers() {
    return CountUtil.count(appUserRepo);
  }
}
