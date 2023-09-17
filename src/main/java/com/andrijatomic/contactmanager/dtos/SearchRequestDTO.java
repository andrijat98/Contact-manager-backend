package com.andrijatomic.contactmanager.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SearchRequestDTO(
    @NotBlank
    String searchParameter,
    @NotBlank
    @Size(min = 3, message = "At least 3 characters are needed for search")
    String searchKeyword,
    @NotNull
    int page,
    @NotNull
    int size,
    @NotBlank
    String sortBy
) {

}
