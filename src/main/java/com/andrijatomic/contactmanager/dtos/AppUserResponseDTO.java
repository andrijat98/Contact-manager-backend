package com.andrijatomic.contactmanager.dtos;

import java.util.List;

public record AppUserResponseDTO(
    String tsid,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    Boolean isPhoneVerified,
    List<AppUserRoleResponseDTO> roles
) {

}
