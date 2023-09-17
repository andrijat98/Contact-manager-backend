package com.andrijatomic.contactmanager.repos;

import com.andrijatomic.contactmanager.models.SmsVerification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneVerificationRepo extends JpaRepository<SmsVerification, Long> {
  Optional<SmsVerification> getSmsVerificationByVerificationCode(String code);
}
