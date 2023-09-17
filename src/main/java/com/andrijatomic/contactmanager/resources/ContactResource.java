package com.andrijatomic.contactmanager.resources;

import com.andrijatomic.contactmanager.dtos.AddContactRequestDTO;
import com.andrijatomic.contactmanager.dtos.ContactResponseDTO;
import com.andrijatomic.contactmanager.dtos.SearchRequestDTO;
import com.andrijatomic.contactmanager.dtos.UpdateContactRequestDTO;
import com.andrijatomic.contactmanager.models.AppUser;
import com.andrijatomic.contactmanager.services.ContactService;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/contact")
@SecurityScheme(name = "user_auth", type = SecuritySchemeType.HTTP, scheme = "basic")
@Tag(name = "Contact resource")
public class ContactResource {

  private final ContactService contactService;

  public ContactResource(ContactService contactService) {
    this.contactService = contactService;
  }
  @Operation(summary = "Get a contact by its TSID",
      description = "Return a contact only if the logged-in user owns it",
      security = {@SecurityRequirement(name = "user_auth")})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the contact",
      content = {
          @Content(mediaType = "application/json",
          examples = {
              @ExampleObject(
                  value = """
                      {
                          "contactTSID": 454216252721226245,
                          "userTSID": 454165907150024959,
                          "firstName": "Example",
                          "lastName": "Contact",
                          "address": "Some address 11",
                          "phoneNumber": "+381031564568",
                          "contactType": "Friend",
                          "contactTypeTsid": 463749531195899274
                      }"""
              )
          }, schema = @Schema(implementation = ContactResponseDTO.class))
      }),
      @ApiResponse(responseCode = "404", ref = "contactNotFound")
  })
  @GetMapping("/get/{tsid}")
  public ResponseEntity<?> getContact(@Parameter(description = "Contact TSID:",
      example = "454216252721226245")
  @PathVariable Long tsid, @AuthenticationPrincipal AppUser loggedInUser) {

    return contactService.getContact(tsid, loggedInUser);
  }

  @Operation(summary = "Get contacts using Pagable",
      description = "Displays the contacts based on Pagable. Returns contacts that user owns.",
      security = {@SecurityRequirement(name = "user_auth")})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found contacts",
          content = {
              @Content(mediaType = "application/json",
                  examples = {
                      @ExampleObject(
                          value = """
                              [
                                  {
                                      "contactTSID": 454894387068030805,
                                      "userTSID": 454575275739236273,
                                      "firstName": "Cathe",
                                      "lastName": "Fessier",
                                      "address": "288 Cambridge Lane",
                                      "phoneNumber": "+3810457484",
                                      "contactType": "Friend",
                                      "contactTypeTsid": 463749531195899274
                                  },
                                  {
                                      "contactTSID": 454894483260196751,
                                      "userTSID": 454575275739236273,
                                      "firstName": "Dael",
                                      "lastName": "Makey",
                                      "address": "58 School Pass",
                                      "phoneNumber": "+3810487404",
                                      "contactType": "Friend",
                                      "contactTypeTsid": 463749531195899274
                                  }
                              ]
                              """
                      )
                  })
          }),
      @ApiResponse(responseCode = "400", ref = "badRequest")
  })
  @GetMapping("/get-all/page/{page}/size/{size}/sort-by/{sortByProperty}")
  public ResponseEntity<List<ContactResponseDTO>> getAllContacts(
      @Parameter(description = "Page number", example = "0") @PathVariable int page,
      @Parameter(description = "Page size", example = "2") @PathVariable int size,
      @Parameter(description = "Property for the contacts to be sorted by", examples = {
          @ExampleObject(value = "firstName", name = "firstName", description = "Sort by first name"),
          @ExampleObject(value = "lastName", name = "lastName", description = "Sort by last name"),
          @ExampleObject(value = "address", name = "address", description = "Sort by address"),
          @ExampleObject(value = "phoneNumber", name = "phoneNumber", description = "Sort by phone number")
      }) @PathVariable String sortByProperty,
      @AuthenticationPrincipal AppUser loggedInUser) {

    return new ResponseEntity<>(
        contactService.getAllContacts(page, size, sortByProperty, loggedInUser), HttpStatus.OK);
  }

  @Operation(summary = "Advanced search for contacts",
      description = "Searches for contacts based on search parameter, search keyword and Pagable.",
      security = {@SecurityRequirement(name = "user_auth")})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found contacts",
          content = {
              @Content(mediaType = "application/json",
                  examples = {
                      @ExampleObject(
                          value = """
                              [
                                  {
                                      "contactTSID": 454216252721226245,
                                      "userTSID": 454165907150024959,
                                      "firstName": "Andrija",
                                      "lastName": "Tomic",
                                      "address": "Neka adresa 45",
                                      "phoneNumber": "+381031564568",
                                      "contactType": "Friend",
                                      "contactTypeTsid": 463749531195899274
                                  },
                                  {
                                      "contactTSID": 454893485758236951,
                                      "userTSID": 454575275739236273,
                                      "firstName": "Andrija",
                                      "lastName": "Tomic",
                                      "address": "Neka adresa 54",
                                      "phoneNumber": "+38112345678",
                                      "contactType": "Friend",
                                      "contactTypeTsid": 463749531195899274
                                  }
                              ]
                              """
                      )
                  })
          }),
      @ApiResponse(responseCode = "400", ref = "badRequest")
  })
  @PostMapping("/search")
  public ResponseEntity<List<ContactResponseDTO>> searchContacts(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      value =
                          """
                          {
                              "searchParameter": "lastName",
                              "searchKeyword": "tom",
                              "page": 0,
                              "size": 10,
                              "sortBy": "firstName"
                          }
                          """
                  )
              }
          )
      )
      @RequestBody @Valid
      SearchRequestDTO searchRequest, @AuthenticationPrincipal AppUser loggedInUser) {
      return new ResponseEntity<>(contactService.searchContacts(searchRequest, loggedInUser),
          HttpStatus.OK);
  }

  @Operation(summary = "Add a contact",
      description = "Adds a contact for the currently logged-in user.",
      security = {@SecurityRequirement(name = "user_auth")})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Added a contact",
          content = {
              @Content(mediaType = "application/json",
                  examples = {
                      @ExampleObject(
                          value =
                              """
                              {
                                  "contactTSID": 456368545407369991,
                                  "userTSID": 453899832266071156,
                                  "firstName": "Garrik",
                                  "lastName": "Domonkos",
                                  "address": "7318 Golf Course Alley",
                                  "phoneNumber": "+381584794",
                                  "contactType": "Friend",
                                  "contactTypeTsid": 463749531195899274
                              }
                              """
                      )
                  })
          }),
      @ApiResponse(responseCode = "400", ref = "badRequest")
  })
  @PostMapping("/add")
  public ResponseEntity<ContactResponseDTO> addContact(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      value =
                          """
                          {
                              "firstName": "Garrik",
                              "lastName": "Domonkos",
                              "address": "7318 Golf Course Alley",
                              "phoneNumber": "+381584794",
                              "contactTypeTsid": 453899597154358142
                          }
                          """
                  )
              }
          )
      )
      @RequestBody @Valid AddContactRequestDTO addContactRequestDTO, @AuthenticationPrincipal
      AppUser loggedInUser) {

    return new ResponseEntity<>(contactService
        .addContact(addContactRequestDTO, loggedInUser), HttpStatus.CREATED);
  }

  @Operation(summary = "Edit an existing contact",
      description = "Edits info for an existing contact. Contact is found by its TSID."
          + " Only contacts that the user owns can be edited.",
      security = {@SecurityRequirement(name = "user_auth")})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated a contact",
          content = {
              @Content(mediaType = "application/json",
                  examples = {
                      @ExampleObject(
                          value =
                              """
                                  {
                                      "contactTSID": 454894586100336103,
                                      "userTSID": 454575275739236273,
                                      "firstName": "Updated",
                                      "lastName": "Contact",
                                      "address": "Example address 10",
                                      "phoneNumber": "+38155533345",
                                      "contactType": "Work",
                                      "contactTypeTsid": 463749531195899274
                                  }
                              """
                      )
                  })
          }),
      @ApiResponse(responseCode = "404", ref = "contactNotFound"),
      @ApiResponse(responseCode = "400", ref = "badRequest")
  })
  @PutMapping("/edit")
  public ResponseEntity<?> editContact(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      value =
                          """
                          {
                               "contactTsid": 454243951043160368,
                               "firstName": "Updated",
                               "lastName": "Contact",
                               "address": "Example address 10",
                               "phoneNumber": "+38155533345",
                               "contactTypeTsid": 453899597158553212
                          }
                          """
                  )
              }
          )
      )
      @RequestBody @Valid UpdateContactRequestDTO contact,
      @AuthenticationPrincipal AppUser loggedInUser) {

    return contactService.updateContact(contact, loggedInUser);
  }

  @Operation(summary = "Delete a contact by its TSID",
      description = "Deletes a contact found by its TSID if the user making the request"
          + " is the contact's owner.",
      security = {@SecurityRequirement(name = "user_auth")})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Deleted the contact", content = {
          @Content(mediaType = "application/json",
              examples = {@ExampleObject(value = "Contact deleted")})
      }),
      @ApiResponse(responseCode = "400", ref = "badRequest"),
      @ApiResponse(responseCode = "404", ref = "contactNotFound")
  })
  @Transactional
  @DeleteMapping("/delete/{tsid}")
  public ResponseEntity<?> deleteContact(@Parameter(description = "Contact TSID:",
      example = "454893592994007175")
      @PathVariable Long tsid,
      @AuthenticationPrincipal AppUser loggedInUser) {

    return contactService.deleteContact(tsid, loggedInUser);
  }

  @Operation(summary = "Export contacts to CSV file",
      description = "Exports current user's contacts to a CSV file.",
      security = {@SecurityRequirement(name = "user_auth")})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Exported contacts to CSV",
          content = {
              @Content(mediaType = "text/csv",
                  examples = {
                      @ExampleObject(
                          value =
                              """
                              Andrija,Tomic,+38112345678,Neka adresa 54,Friend
                              Cathe,Fessier,+3810457484,288 Cambridge Lane,Home
                              Contact,Two,+38112345678,Neka adresa 1,Friend
                              Dael,Makey,+3810487404,58 School Pass,Friend
                              Garrik,Domonkos,+381584794,7318 Golf Course Alley,Friend
                              Updated,Contact,+38155533345,Example address 10,Work
                              """
                      )
                  })
          }),
      @ApiResponse(responseCode = "400", ref = "badRequest"),
      @ApiResponse(responseCode = "500", ref = "internalServerError")
  })
  @GetMapping("/exportcsv")
  public void exportContactsToCsv(HttpServletResponse response,
      @AuthenticationPrincipal AppUser loggedInUser)
      throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {

    contactService.exportContactsToCsv(loggedInUser, response);
  }

  @Operation(summary = "Import contacts from a CSV file",
      description = "Adds contacts to the logged in user from an uploaded CSV file.",
      security = {@SecurityRequirement(name = "user_auth")})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Contacts added"),
      @ApiResponse(responseCode = "400", ref = "badRequest"),
      @ApiResponse(responseCode = "500", ref = "internalServerError")
  })
  @PostMapping(value = "/importcsv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> importContactsFromCsvFile(
      @RequestParam MultipartFile file,
      @AuthenticationPrincipal AppUser loggedInUser) {

    return contactService.importContactsFromCsvFile(file, loggedInUser);

  }

  @Operation(summary = "Count contacts",
      description = "Counts the number of contacts that user has.",
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
  public ResponseEntity<?> countAllContacts() {
    return contactService.countAllContacts();
  }
}
