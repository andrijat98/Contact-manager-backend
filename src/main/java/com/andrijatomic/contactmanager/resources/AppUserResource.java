package com.andrijatomic.contactmanager.resources;

import com.andrijatomic.contactmanager.dtos.AddAppUserRequestDTO;
import com.andrijatomic.contactmanager.dtos.AppUserResponseDTO;
import com.andrijatomic.contactmanager.dtos.ContactResponseDTO;
import com.andrijatomic.contactmanager.dtos.UpdateAppUserRequestDTO;
import com.andrijatomic.contactmanager.models.AppUser;
import com.andrijatomic.contactmanager.services.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@SecurityScheme(name = "user_auth", type = SecuritySchemeType.HTTP, scheme = "basic")
@Tag(name = "App user resource")
public class AppUserResource {

  private final AppUserService appUserService;

  public AppUserResource(AppUserService appUserService) {
    this.appUserService = appUserService;
  }

  @Operation(summary = "Get a user by its TSID",
      description = "Returns a user based on its TSID.",
      security = {@SecurityRequirement(name = "user_auth")})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the user",
          content = {
              @Content(mediaType = "application/json",
                  examples = {
                      @ExampleObject(
                          value =
                              """
                              {
                                  "tsid": "453899832266071156",
                                  "firstName": "Admin",
                                  "lastName": "LastName",
                                  "email": "adminuser1@hotmail.com",
                                  "roles": [
                                      {
                                          "id": 2,
                                          "tsid": 453899597133387633,
                                          "roleName": "ROLE_ADMIN"
                                      }
                                  ],
                                  "contacts": [
                                      {
                                          "contactTSID": 456368545407369991,
                                          "userTSID": 453899832266071156,
                                          "firstName": "Garrik",
                                          "lastName": "Domonkos",
                                          "address": "7318 Golf Course Alley",
                                          "phoneNumber": "+381584794",
                                          "contactType": "Friend"
                                      }
                                  ]
                              }
                              """
                      )
                  }, schema = @Schema(implementation = ContactResponseDTO.class))
          }),
      @ApiResponse(responseCode = "404", ref = "appUserNotFound"),
      @ApiResponse(responseCode = "400", ref = "badRequest")
  })
  @GetMapping("/get/{tsid}")
  public ResponseEntity<AppUserResponseDTO> getAppUser(
      @Parameter(description = "User TSID:", example = "453899832266071156")
      @PathVariable Long tsid) {
    return new ResponseEntity<>(appUserService.getAppUser(tsid), HttpStatus.OK);
  }

  @Operation(summary = "Get users using Pagable",
      description = "Displays the users based on Pagable.",
      security = {@SecurityRequirement(name = "user_auth")})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found users",
          content = {
              @Content(mediaType = "application/json",
                  examples = {
                      @ExampleObject(
                          value = """
                              [
                                  {
                                      "tsid": "453899832266071156",
                                      "firstName": "Admin",
                                      "lastName": "LastName",
                                      "email": "adminuser1@hotmail.com",
                                      "roles": [
                                          {
                                              "id": 2,
                                              "tsid": 453899597133387633,
                                              "roleName": "ROLE_ADMIN"
                                          }
                                      ],
                                      "contacts": []
                                  },
                                  {
                                      "tsid": "454165907150024959",
                                      "firstName": "Regular",
                                      "lastName": "User",
                                      "email": "regularuser1@hotmail.com",
                                      "roles": [
                                          {
                                              "id": 1,
                                              "tsid": 454165907464597904,
                                              "roleName": "ROLE_USER"
                                          }
                                      ],
                                      "contacts": []
                                  }
                              ]
                              """
                      )
                  })
          }),
      @ApiResponse(responseCode = "400", ref = "badRequest")
  })
  @GetMapping("/get-all/page/{page}/size/{size}/sort-by/{sortByProperty}")
  public ResponseEntity<List<AppUserResponseDTO>> getAllAppUsers(
      @Parameter(description = "Page number", example = "0") @PathVariable int page,
      @Parameter(description = "Page size", example = "2") @PathVariable int size,
      @Parameter(description = "Property for the user to be sorted by", examples = {
          @ExampleObject(value = "firstName", name = "firstName", description = "Sort by first name"),
          @ExampleObject(value = "lastName", name = "lastName", description = "Sort by last name"),
          @ExampleObject(value = "email", name = "email", description = "Sort by email")
      }) @PathVariable String sortByProperty
  ) {

    return new ResponseEntity<>(appUserService
        .getAllAppUsers(page, size, sortByProperty), HttpStatus.OK);
  }

  @Operation(summary = "Add a user",
      description = "Adds a user.",
      security = {@SecurityRequirement(name = "user_auth")})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Added a user",
          content = {
              @Content(mediaType = "application/json",
                  examples = {
                      @ExampleObject(
                          value =
                              """
                              {
                                  "tsid": "456394668800627516",
                                  "firstName": "Example",
                                  "lastName": "User",
                                  "email": "exampleuser@hotmail.com",
                                  "roles": [
                                      {
                                          "id": 1,
                                          "tsid": 454165907464597904,
                                          "roleName": "ROLE_USER"
                                      }
                                  ],
                                  "contacts": []
                              }
                              """
                      )
                  })
          }),
      @ApiResponse(responseCode = "400", ref = "badRequest"),
      @ApiResponse(responseCode = "500", ref = "internalServerError")
  })
  @PostMapping("/add")
  public ResponseEntity<?> addAppUser(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      value =
                          """
                          {
                              "firstName": "Example",
                              "lastName": "User",
                              "password": "exampleuser12345",
                              "email": "exampleuser@hotmail.com",
                              "phoneNumber": "+3816551478",
                              "userRoleTsid": 454165907464597904
                          }
                          """
                  )
              }
          )
      )
      @RequestBody @Valid AddAppUserRequestDTO addAppUserRequestDTO){

    try {
      return appUserService.addAppUser(addAppUserRequestDTO);
    } catch (MessagingException e) {
      return new ResponseEntity<>("Failed to add user", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Edit an existing user",
      description = "Edits details of an existing user. If the user making the request has ADMIN"
          + " role, edited user will be found by providing its TSID in the request. Otherwise,"
          + " user to be edited is the one that's making the request. All users can change"
          + " each of the fields individually except for email which can only be changed by"
          + " an admin. Changing email disables the account until the email is verified."
          + " Changing phone number sets the is_phone_verified field to false.",
      security = {@SecurityRequirement(name = "user_auth")})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated a user",
          content = {
              @Content(mediaType = "application/json",
                  examples = {
                      @ExampleObject(
                          value =
                              """
                              {
                                  "tsid": "456394668800627516",
                                  "firstName": "Updated",
                                  "lastName": "User",
                                  "email": "updateduser1@gmail.com",
                                  "roles": [
                                      {
                                          "id": 1,
                                          "tsid": 454165907464597904,
                                          "roleName": "ROLE_USER"
                                      }
                                  ],
                                  "contacts": []
                              }
                              """
                      )
                  })
          }),
      @ApiResponse(responseCode = "400", ref = "badRequest"),
      @ApiResponse(responseCode = "404", ref = "appUserNotFound")

  })
  @PutMapping("/edit")
  public ResponseEntity<AppUserResponseDTO> editAppUser(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      value =
                          """
                              {
                              "tsid": 456394668800627516,
                              "firstName": "Updated",
                              "lastName": "User",
                              "password": "someotherpassword",
                              "phoneNumber": "+46585965895",
                              "email": "updateduser1@gmail.com"
                              }
                          """
                  )
              }
          )
      )
      @RequestBody @Valid UpdateAppUserRequestDTO updateAppUserRequestDTO, @AuthenticationPrincipal
      AppUser loggedInUser) {

    return new ResponseEntity<>(appUserService
        .updateAppUser(updateAppUserRequestDTO, loggedInUser), HttpStatus.OK);
  }

  @Operation(summary = "Delete a user by its TSID",
      description = "Deletes a user found by its TSID.",
      security = {@SecurityRequirement(name = "user_auth")})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Deleted the user", content = {
          @Content(mediaType = "application/json",
              examples = {@ExampleObject(value = "User deleted")})
      }),
      @ApiResponse(responseCode = "400", ref = "badRequest"),
      @ApiResponse(responseCode = "404", ref = "appUserNotFound")
  })
  @DeleteMapping("/delete/{tsid}")
  public ResponseEntity<?> deleteAppUser(
      @Parameter(description = "User TSID:", example = "456394668800627516")
      @PathVariable Long tsid) {
    appUserService.deleteAppUser(tsid);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(summary = "Get all user roles",
      description = "Returns all the roles a user can have",
      security = {@SecurityRequirement(name = "user_auth")})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found roles",
          content = {
              @Content(mediaType = "application/json",
                  examples = {
                      @ExampleObject(
                          value = """
                                  [
                                      {
                                          "roleTsid": "453899597133387633",
                                          "roleName": "ROLE_USER"
                                      },
                                      {
                                          "roleTsid": "454165907464597904",
                                          "roleName": "ROLE_ADMIN"
                                      }
                                  ]
                                """
                      )
                  })
          }),
      @ApiResponse(responseCode = "400", ref = "badRequest")
  })
  @GetMapping("/get-roles")
  public ResponseEntity<?> getAllRoles() {
    return appUserService.getAllRoles();
  }

  @Operation(summary = "Count user",
      description = "Counts the number of users.",
      security = {@SecurityRequirement(name = "user_auth")})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          content = {
              @Content(mediaType = "application/json",
                  examples = {
                      @ExampleObject(
                          value =
                              """
                                 {
                                    "count": 20
                                 }
                              """
                      )
                  })
          }),
      @ApiResponse(responseCode = "404")
  })
  @GetMapping("/count")
  public ResponseEntity<?> countAllAppUsers() {
    return appUserService.countAllAppUsers();
  }
}
