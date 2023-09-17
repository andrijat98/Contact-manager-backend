package com.andrijatomic.contactmanager.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateAppUserRequestDTO(
    @Pattern(regexp = "^[0-9]+$", message = "User role TSID can ony contain numbers")
    String tsid,
    @Pattern(regexp = "^[a-zA-Z]+$",
        message = "First name can only contain letters of the alphabet without spaces")
    String firstName,
    @Pattern(regexp = "^[a-zA-Z]+$",
        message = "Last name can only contain letters of the alphabet without spaces")
    String lastName,
    @Size(min = 10, max = 20,
        message = "Password must be between 10 and 20 characters in length")
    String password,
    @Size(max = 30, message = "Max size is 30 characters")
    @Email(message = "Must be a valid email")
    String email,
    @Pattern(regexp = "^\\+[0-9]{9,14}$",
        message = "Phone must have '+' followed by 9 to 14 digits, example: +314584814848")
    String phoneNumber,
    String[] userRoleTsids
){

}
