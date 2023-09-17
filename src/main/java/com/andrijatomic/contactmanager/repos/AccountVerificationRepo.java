package com.andrijatomic.contactmanager.repos;

import com.andrijatomic.contactmanager.models.AccountVerification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountVerificationRepo extends JpaRepository<AccountVerification, Long> {
  Optional<AccountVerification> getAccountVerificationByVerificationUrl(String verificationUrl);
}
