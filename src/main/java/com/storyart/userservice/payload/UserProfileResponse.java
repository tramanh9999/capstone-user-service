package com.storyart.userservice.payload;

import com.storyart.userservice.common.constants.RoleName;
import com.storyart.userservice.model.User;
import com.storyart.userservice.service.BeanUtil;
import com.storyart.userservice.service.RoleService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;


/*show infromation to client to display user profile*/

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Integer id;
    private String username;
    private String name;
    private String email;
    private Date jointAt;
    private Integer storyCount;
    private boolean isActive;

    private String profileImage;

    private String intro_content;
    private RoleName role;
    private String  avatar;
//todo: bo username
    //todo: slug
    //todo: them role


    public UserProfileResponse(User user) {
        this.setId(user.getId());
        this.setName(user.getName());
        this.setUsername(user.getUsername());
        this.setEmail(user.getEmail());
        this.setJointAt(user.getCreatedAt());
        this.setIntro_content(user.getIntroContent());
        this.setActive(user.isActive());
              this.role = BeanUtil.getBean(RoleService.class).findRoleById(user.getRoleId()).getName();
this.setAvatar(user.getAvatar());
this.setProfileImage(user.getProfileImage());
    }



}
