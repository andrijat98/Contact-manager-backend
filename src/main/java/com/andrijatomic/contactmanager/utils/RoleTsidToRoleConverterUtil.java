package com.andrijatomic.contactmanager.utils;

import com.andrijatomic.contactmanager.exceptions.RoleNotFoundException;
import com.andrijatomic.contactmanager.models.Role;
import com.andrijatomic.contactmanager.repos.RoleRepo;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RoleTsidToRoleConverterUtil {

  public static List<Role> convertToRoles (String[] roleTsids, RoleRepo roleRepo) {
    return Arrays.stream(roleTsids)
        .filter(
            s -> {
              try {
                Long.parseLong(s);
                return true;
              } catch (NumberFormatException e) {
                return false;
              }
            }
        )
        .map(s -> {
          var roleTsid = Long.parseLong(s);
          return roleRepo.getRoleByTsid(roleTsid).orElseThrow(() -> new RoleNotFoundException(roleTsid));
        })
        .collect(Collectors.toList());
  }
}
