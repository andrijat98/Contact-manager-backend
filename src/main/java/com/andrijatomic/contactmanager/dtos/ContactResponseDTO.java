package com.andrijatomic.contactmanager.dtos;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvIgnore;

public record ContactResponseDTO(
    @CsvIgnore
    String contactTsid,
    @CsvBindByPosition(position = 0)
    String firstName,
    @CsvBindByPosition(position = 1)
    String lastName,
    @CsvBindByPosition(position = 3)
    String address,
    @CsvBindByPosition(position = 2)
    String phoneNumber,
    @CsvBindByPosition(position = 4)
    String contactType,
    @CsvIgnore
    String contactTypeTsid
) {

}
