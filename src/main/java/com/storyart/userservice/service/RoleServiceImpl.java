package com.storyart.userservice.service;

import com.storyart.userservice.common.constants.RoleName;
import com.storyart.userservice.model.Role;
import com.storyart.userservice.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleRepository roleRepository;

    @Override
    public Role findRoleById(Integer id) {
        return roleRepository.findRoleById(id).orElse(null);
    }

    @Override
    public Role findRoleByRoleName(RoleName roleName) {
       return  roleRepository.findRoleByName(roleName).orElse(null);
    }

    @Override
    public void createDefaultRoles() {
        if(roleRepository.findAll().size() != 0) return;
        for(RoleName role: RoleName.values()){
            Role r = new Role();
            r.setName(role);
            roleRepository.save(r);
        }
    }
}
