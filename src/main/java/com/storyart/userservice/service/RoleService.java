package com.storyart.userservice.service;

import com.storyart.userservice.common.constants.RoleName;
import com.storyart.userservice.model.Role;

public interface RoleService {
    Role findRoleById(Integer id);

    Role findRoleByRoleName(RoleName roleName);

    void createDefaultRoles();
}
