package com.andrijatomic.contactmanager.dtos;

import jakarta.validation.constraints.NotBlank;

public record AddContactTypeRequestDTO(
    @NotBlank(message = "Contact type must not be blank")
    String type
) {

}
