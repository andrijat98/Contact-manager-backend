package com.andrijatomic.contactmanager.dtos;

import com.opencsv.bean.CsvBindByPosition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddContactCsvDTO {
    @NotBlank
    @Size(max = 20, message = "Max size is 20 characters")
    @Pattern(regexp = "^[a-zA-Z]+$",
        message = "First name can only contain letters of the alphabet without spaces")
    @CsvBindByPosition(position = 0)
    String firstName;

    @NotBlank
    @Size(max = 20, message = "Max size is 20 characters")
    @Pattern(regexp = "^[a-zA-Z]+$",
        message = "Last name can only contain letters of the alphabet without spaces")
    @CsvBindByPosition(position = 1)
    String lastName;

    @NotBlank
    @Pattern(regexp = "^\\+[0-9]{9,14}$",
        message = "Phone must have '+' followed by 9 to 14 digits, example: +314584814848")
    @CsvBindByPosition(position = 2)
    String phoneNumber;

    @NotBlank
    @Size(max = 30, message = "Max size is 30 characters")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$",
        message = "Address can only contain letters and numbers")
    @CsvBindByPosition(position = 3)
    String address;
    @NotBlank
    @CsvBindByPosition(position = 4)
    String contactType;
}
