package com.storyart.userservice.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.storyart.userservice.common.constants.RoleName;
import com.storyart.userservice.model.Role;
import com.storyart.userservice.repository.RoleRepository;
import com.storyart.userservice.service.BeanUtil;
import com.storyart.userservice.service.RoleService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private Integer id;

    private String username;

    @JsonIgnore
    private String password;


    private String name;
    private RoleName roleName;


    public static UserPrincipal create(com.storyart.userservice.model.User user) {
        Set<Role> roles= new HashSet<>();
        roles.add(BeanUtil.getBean(RoleService.class).findRoleById(user.getRoleId()));

        List<GrantedAuthority> grantedAuthorityList = roles.stream().
                map(role ->new SimpleGrantedAuthority(role.getName().name()))
                    .collect(Collectors.toList());
        return new UserPrincipal(user.getId(), user.getUsername(), user.getPassword(),user.getName(),BeanUtil.getBean(RoleRepository.class).findRoleById(user.getRoleId()).get().getName(),  grantedAuthorityList);
    }

    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
