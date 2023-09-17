package com.andrijatomic.contactmanager.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddAppUserRequestDTO(
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
    @Size(min = 10, max = 20,
    message = "Password must be between 10 and 20 characters in length")
    String password,
    @Email(message = "Must be a valid email")
    @Size(max = 30, message = "Max size is 30 characters")
    String email,
    @NotBlank
    @Pattern(regexp = "^\\+[0-9]{9,14}$",
        message = "Phone must have '+' followed by 9 to 14 digits, example: +314584814848")
    String phoneNumber,
    @NotNull(message = "Array can't be null")
    @Size(min = 1, message = "The array must contain at least one element")
    String[] userRoleTsids
) {
}
