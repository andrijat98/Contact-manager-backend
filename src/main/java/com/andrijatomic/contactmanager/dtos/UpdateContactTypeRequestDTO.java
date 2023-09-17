package com.andrijatomic.contactmanager.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateContactTypeRequestDTO(

    @NotNull(message = "Contact type TSID must not be null")
    Long tsid,
    @NotBlank(message = "Contact type must not be blank")
    String type
) {

}
