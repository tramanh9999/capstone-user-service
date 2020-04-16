package com.storyart.userservice.payload;


import com.storyart.userservice.common.constants.RoleName;
import com.storyart.userservice.model.User;
import com.storyart.userservice.service.BeanUtil;
import com.storyart.userservice.service.RoleService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInManagementResponse {
    private Integer id;
    private String username;
    private String name;
    private RoleName role;

    private boolean isActive;
    private boolean isDeactiveByAdmin;
    private String email;
    private Date jointAt;





    public UserInManagementResponse(User u) {
        this.id=u.getId();
        this.email= u.getEmail();
        this.isActive= u.isActive();
        this.name= u.getName();
        this.username= u.getUsername();
        this.role = BeanUtil.getBean(RoleService.class).findRoleById(u.getRoleId()).getName();
        this.jointAt = u.getCreatedAt();
        this.isDeactiveByAdmin = u.isDeactiveByAdmin();
    }
}
