package com.andrijatomic.contactmanager.services;

import com.andrijatomic.contactmanager.models.AccountVerification;
import com.andrijatomic.contactmanager.models.AppUser;
import com.andrijatomic.contactmanager.models.SmsVerification;
import com.andrijatomic.contactmanager.repos.AccountVerificationRepo;
import com.andrijatomic.contactmanager.repos.AppUserRepo;
import com.andrijatomic.contactmanager.repos.PhoneVerificationRepo;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountVerificationService {
  private final AccountVerificationRepo accountVerificationRepo;
  private final AppUserRepo appUserRepo;
  private final PhoneVerificationRepo phoneVerificationRepo;
  private final SmsService smsService;

  public AccountVerificationService(AccountVerificationRepo accountVerificationRepo,
      AppUserRepo appUserRepo, PhoneVerificationRepo phoneVerificationRepo, SmsService smsService) {
    this.accountVerificationRepo = accountVerificationRepo;
    this.appUserRepo = appUserRepo;
    this.phoneVerificationRepo = phoneVerificationRepo;
    this.smsService = smsService;
  }

  public AccountVerification createVerifyLink(AppUser appUser) {
    return accountVerificationRepo.save(new AccountVerification(null, appUser,
          UUID.randomUUID().toString(), LocalDateTime.now()));
  }

  public boolean verifyAccount(String uuid) {
    Optional<AccountVerification> verification =
        accountVerificationRepo.getAccountVerificationByVerificationUrl(uuid);

    if (verification.isPresent() && checkIfNotExpired(verification.get().getDate(), 24)) {

      AppUser user = verification.get().getAppUser();
      user.setEnabled(true);

      appUserRepo.save(user);

      return true;
    }
    return false;
  }

  public ResponseEntity<?> createPhoneVerificationCode(AppUser loggedInUser) {

    if (!loggedInUser.isPhoneVerified()) {
      String verificationCode = smsService.sendSms(loggedInUser.getPhoneNumber());
      phoneVerificationRepo.save(new SmsVerification(null, loggedInUser, verificationCode, LocalDateTime.now()));
      return new ResponseEntity<>(HttpStatus.OK);
    }

    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  public boolean verifyPhoneNumber(String verificationCode) {
    Optional<SmsVerification> verification = phoneVerificationRepo
        .getSmsVerificationByVerificationCode(verificationCode);

    if (verification.isPresent() && checkIfNotExpired(verification.get().getDate(), 1)) {

      AppUser user = verification.get().getAppUser();
      user.setPhoneVerified(true);
      appUserRepo.save(user);
      return true;
    }
    return false;
  }



  private boolean checkIfNotExpired(LocalDateTime dateCreated, int validFor) {
    Duration duration = Duration.between(dateCreated, LocalDateTime.now());
    return duration.toHours() < validFor;
  }
}
