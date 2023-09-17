package com.andrijatomic.contactmanager.resources;

import com.andrijatomic.contactmanager.dtos.AddContactTypeRequestDTO;
import com.andrijatomic.contactmanager.dtos.ContactTypeResponseDTO;
import com.andrijatomic.contactmanager.dtos.UpdateContactTypeRequestDTO;
import com.andrijatomic.contactmanager.services.ContactTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contact-type")
@SecurityScheme(name = "user_auth", type = SecuritySchemeType.HTTP, scheme = "basic")
@Tag(name = "Contact type resource")
public class ContactTypeResource {

  private final ContactTypeService contactTypeService;

  public ContactTypeResource(ContactTypeService contactTypeService) {
    this.contactTypeService = contactTypeService;
  }

  @Operation(summary = "Get all contact types",
      description = "Returns list of all contact types.",
      security = {@SecurityRequirement(name = "user_auth")})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found contact types",
          content = {
              @Content(mediaType = "application/json",
                  examples = {
                      @ExampleObject(
                          value =
                              """
                                  [
                                      {
                                          "tsid": 463749531195899260,
                                          "type": "Home"
                                      },
                                      {
                                          "tsid": 463749531195899274,
                                          "type": "Friend"
                                      },
                                      {
                                          "tsid": 463749582274133950,
                                          "type": "Family"
                                      }
                                  ]
                              """
                      )
                  })
          }),
      @ApiResponse(responseCode = "404", description = "No contact types found",
          content = {
              @Content(mediaType = "application/json",
                  examples = {
                      @ExampleObject (
                          value =
                              """
                              {
                              "code": 404,
                              "Status": "Not found",
                              "Message": "Contact types not found."
                              }
                              """
                      )
                  }
              )
          })
  })
  @GetMapping("/get-all")
  public ResponseEntity<?> getAllContactTypes() {
    return contactTypeService.getAllContactTypes();
  }

  @Operation(summary = "Add a contact type",
      description = "Adds a new contact type",
      security = {@SecurityRequirement(name = "user_auth")})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Contact type added",
          content = {
              @Content(mediaType = "application/json",
                  examples = {
                      @ExampleObject(
                          value =
                              """
                                  {
                                      "tsid": 463950699807764531,
                                      "type": "Company"
                                  }
                              """
                      )
                  })
          })
  })
  @PostMapping("/add")
  public ResponseEntity<ContactTypeResponseDTO> addContactType(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      value =
                          """
                              {
                                  "type": "Company"
                              }
                          """
                  )
              }
          )
      )
      @RequestBody @Valid AddContactTypeRequestDTO requestDTO
  ) {
    return contactTypeService.addContactType(requestDTO);
  }
  @Operation(summary = "Update contact type",
      description = "Updates an existing contact type contact type",
      security = {@SecurityRequirement(name = "user_auth")})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Contact type updated",
          content = {
              @Content(mediaType = "application/json",
                  examples = {
                      @ExampleObject(
                          value =
                              """
                                  {
                                      "tsid": 463749582274133950,
                                      "type": "Family"
                                  }
                              """
                      )
                  })
          }),
      @ApiResponse(responseCode = "404", description = "Contact type not found",
          content = {
              @Content(mediaType = "application/json",
                  examples = {
                      @ExampleObject (
                          value =
                              """
                              {
                              "code": 404,
                              "Status": "Not found",
                              "Message": "Contact type not found."
                              }
                              """
                      )
                  }
              )
          })
  })
  @PutMapping("/update")
  public ResponseEntity<ContactTypeResponseDTO> updateContactType(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      value =
                          """
                              {
                                  "tsid": 463749582274133950,
                                  "type": "Family"
                              }
                          """
                  )
              }
          )
      )
      @RequestBody @Valid UpdateContactTypeRequestDTO requestDTO
  ) {
    return contactTypeService.updateContactType(requestDTO);
  }
}
