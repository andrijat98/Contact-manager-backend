package com.andrijatomic.contactmanager.resources;

import com.andrijatomic.contactmanager.models.AppUser;
import com.andrijatomic.contactmanager.services.AccountVerificationService;
import com.andrijatomic.contactmanager.services.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RestController
@RequestMapping("/verification")
@SecurityScheme(name = "user_auth", type = SecuritySchemeType.HTTP, scheme = "basic")
@Tag(name = "Account Verification Resource")
public class VerificationResource {
  private final AccountVerificationService verificationService;
  private final EmailService emailService;
  private final TemplateEngine templateEngine;

  public VerificationResource(AccountVerificationService verificationService,
      EmailService emailService, TemplateEngine templateEngine) {
    this.verificationService = verificationService;
    this.emailService = emailService;
    this.templateEngine = templateEngine;
  }

  @Operation(summary = "Verify user email", description = "Verifies user by checking provided uuid.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User verified"),
      @ApiResponse(responseCode = "403", description =
          "Fails when the verification uuid doesn't exist or the link expired (24 hours)",
      content = {
          @Content(mediaType = "application/json",
              examples = {
                  @ExampleObject (
                      value =
                          """
                          {
                          "code": 403,
                          "Status": "Forbidden",
                          "Message": "Verification failed, ask admin for new verification mail"
                          }
                          """
                  )
              }
          )
      })
  })
  @GetMapping("/verify/{uuid}")
  public ResponseEntity<?> verifyUser(
      @Parameter(description = "User UUID:", example = "ea277617-a678-4742-8ec5-d15b67726582")
      @PathVariable String uuid) {
    if (verificationService.verifyAccount(uuid)) {

      String htmlContent = templateEngine
          .process("verification/verification-success", new Context());

      return ResponseEntity.status(HttpStatus.OK)
          .body(htmlContent);
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body("Verification failed, ask admin for new verification mail");
  }

  @Operation(summary = "Get new verification email",
      description = "Admin requests for a new user verification mail to be sent to the user.",
      security = {@SecurityRequirement(name = "user_auth")})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully sent a new verification mail",
      content = {
          @Content(mediaType = "application/json",
              examples = {
                  @ExampleObject (
                      value =
                          """
                          {
                          "code": 200,
                          "Status": "Ok",
                          "Message": "Sent a new verification mail."
                          }
                          """
                  )
              }
          )
      }),
      @ApiResponse(responseCode = "400", description = "User is already verified",
      content = {
          @Content(mediaType = "application/json",
              examples = {
                  @ExampleObject (
                      value =
                          """
                          {
                          "code": 400,
                          "Status": "Bad Request",
                          "Message": "User is already verified."
                          }
                          """
                  )
              }
          )
      })
  })
  @GetMapping("/generate-new/{tsid}")
  public ResponseEntity<?> generateNewVerificationEmail(
      @Parameter(description = "User TSID:", example = "453899832266071156")
      @PathVariable Long tsid) {
    return emailService.generateNewVerificationEmail(tsid);
  }

  @Operation(summary = "Getting phone verification code",
      description = "Sends a verification code to the phone number of the user making the request.",
      security = {@SecurityRequirement(name = "user_auth")})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Verification code sent.",
          content = {
              @Content(mediaType = "application/json",
                  examples = {
                      @ExampleObject (
                          value =
                              """
                              {
                              "code": 200,
                              "Status": "Ok",
                              "Message": "Verification code sent."
                              }
                              """
                      )
                  }
              )
          }),
      @ApiResponse(responseCode = "400", description = "Phone already verified.",
          content = {
              @Content(mediaType = "application/json",
                  examples = {
                      @ExampleObject (
                          value =
                              """
                              {
                              "code": 400,
                              "Status": "Bad Request",
                              "Message": "Phone already verified."
                              }
                              """
                      )
                  }
              )
          })
  })
  @GetMapping("/get-phone-verification-code")
  public ResponseEntity<?> getPhoneVerificationCode(@AuthenticationPrincipal AppUser loggedInUser) {
    return verificationService.createPhoneVerificationCode(loggedInUser);
  }

  @Operation(summary = "Verifying phone number",
      description = "Verifies a phone number by sending verification code that was sent to the"
          + " user's phone.",
      security = {@SecurityRequirement(name = "user_auth")})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Phone number verified"),
      @ApiResponse(responseCode = "401", description = "Failed verification",
          content = {
              @Content(mediaType = "application/json",
                  examples = {
                      @ExampleObject (
                          value =
                              """
                              {
                              "code": 401,
                              "Status": "Unauthorized",
                              "Message": "Verification failed, request a new verification code"
                              }
                              """
                      )
                  }
              )
          })
  })
  @GetMapping("/verify-phone/{verificationCode}")
  public ResponseEntity<?> verifyPhone (
      @Parameter(description = "Verification code:", example = "347604")
      @PathVariable String verificationCode) {
    if (verificationService.verifyPhoneNumber(verificationCode)) {

      return ResponseEntity.status(HttpStatus.OK).build();
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

}
