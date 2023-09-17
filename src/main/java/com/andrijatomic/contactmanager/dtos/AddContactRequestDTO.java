package com.andrijatomic.contactmanager.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddContactRequestDTO(
    @NotBlank
    @Size(max = 20, message = "Max size is 20 characters")
    @Pattern(regexp = "^[a-zA-Z]+$",
        message = "First name can only contain letters of the alphabet without spaces")
    String firstName,
    @NotBlank
    @Size(max = 20, message = "Max size is 20 characters")
    @Pattern(regexp = "^[a-zA-Z]+$",
        message = "Last name can only contain letters of the alphabet without spaces")
    String lastName,
    @NotBlank
    @Size(max = 30, message = "Max size is 30 characters")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$",
        message = "Address can only contain letters and numbers")
    String address,
    @NotBlank
    @Pattern(regexp = "^\\+[0-9]{9,14}$",
        message = "Phone must have '+' followed by 9 to 14 digits, example: +314584814848")
    String phoneNumber,
    @Pattern(regexp = "^[0-9]+$", message = "Contact type TSID can ony contain numbers")
    @NotBlank(message = "Contact type TSID can not be blank")
    String contactTypeTsid
) {

}
