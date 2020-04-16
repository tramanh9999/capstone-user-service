package com.storyart.userservice.dto;

import com.storyart.userservice.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {
    private Integer id;
    private String username;
    private String name;
    private String password;
    private String avatar;
    private String profileImage;
    private int roleId;
    private String introContent;
    private boolean isActive;
    private String email;
    private Date createdAt;
    private Date updatedAt;
    boolean isDeactiveByAdmin;

    private Role role;

}
