package com.andrijatomic.contactmanager.resources;

import com.andrijatomic.contactmanager.dtos.AppUserResponseDTO;
import com.andrijatomic.contactmanager.models.AppUser;
import com.andrijatomic.contactmanager.utils.AppUserResponseDTOMapper;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@SecurityScheme(name = "user_auth", type = SecuritySchemeType.HTTP, scheme = "basic")
@Tag(name = "Authentication Resource")
public class AuthenticationResource {

  private final AppUserResponseDTOMapper responseMapper;

  public AuthenticationResource(AppUserResponseDTOMapper responseMapper) {
    this.responseMapper = responseMapper;
  }

  @Operation(summary = "Login",
      description = "After successful login, returns logged in user's info",
      security = {@SecurityRequirement(name = "user_auth")})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successful login",
          content = {
              @Content(mediaType = "application/json",
                  examples = {
                      @ExampleObject(
                          value =
                              """
                                  {
                                      "tsid": "454165907150024959",
                                      "firstName": "Regular",
                                      "lastName": "User",
                                      "email": "regularuser1@gmail.com",
                                      "phoneNumber": "+3547897897",
                                      "isPhoneVerified": false,
                                      "roles": [
                                          {
                                              "roleTsid": "453899597133387633",
                                              "roleName": "ROLE_USER"
                                          }
                                      ]
                                  }
                              """
                      )
                  })
          }),
      @ApiResponse(responseCode = "401", description = "Incorrect email and/or password")
  })
  @GetMapping
  public ResponseEntity<AppUserResponseDTO> login (
      @AuthenticationPrincipal AppUser userMakingLoginRequest
  ) {
    return new ResponseEntity<>(responseMapper.apply(userMakingLoginRequest), HttpStatus.OK);
  }

}
