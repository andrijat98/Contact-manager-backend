package com.andrijatomic.contactmanager.services;

import com.andrijatomic.contactmanager.exceptions.AppUserNotFoundException;
import com.andrijatomic.contactmanager.models.AccountVerification;
import com.andrijatomic.contactmanager.models.AppUser;
import com.andrijatomic.contactmanager.repos.AppUserRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

  private final TemplateEngine templateEngine;
  private final JavaMailSender javaMailSender;
  private final AccountVerificationService verificationService;
  private final AppUserRepo appUserRepo;

  public EmailService(TemplateEngine templateEngine, JavaMailSender javaMailSender,
      AccountVerificationService verificationService, AppUserRepo appUserRepo) {
    this.templateEngine = templateEngine;
    this.javaMailSender = javaMailSender;
    this.verificationService = verificationService;
    this.appUserRepo = appUserRepo;
  }
  @Value("${spring.mail.username}")
  private String from;

  public void sendMail(AppUser user, AccountVerification verification) throws MessagingException {
    Context context = new Context();
    context.setVariable("user", user);
    context.setVariable("verification",
        "http://localhost:8080/verification/verify/" + verification.getVerificationUrl());

    String process = templateEngine.process("verification/verifymail", context);
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
    helper.setFrom(from);
    helper.setSubject("Verify mail");
    helper.setText(process, true);
    helper.setTo(user.getEmail());
    javaMailSender.send(mimeMessage);
  }

  @Transactional
  public ResponseEntity<?> generateNewVerificationEmail(Long tsid){

    AppUser user = appUserRepo.getAppUserByTsid(tsid)
        .orElseThrow(() -> new AppUserNotFoundException("tsid", tsid.toString()));

    if (!user.isEnabled()) {
      AccountVerification verification = verificationService.createVerifyLink(user);
      try {
        sendMail(user, verification);
      } catch (MessagingException e) {
        throw new RuntimeException();
      }
      return ResponseEntity.status(HttpStatus.OK).body("Sent a new verification email");
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is already verified");

  }
}
