package com.storyart.userservice.controller;


import com.storyart.userservice.common.constants.RoleName;
import com.storyart.userservice.exception.AppException;
import com.storyart.userservice.exception.BadRequestException;
import com.storyart.userservice.exception.ResourceNotFoundException;
import com.storyart.userservice.model.Role;
import com.storyart.userservice.model.User;
import com.storyart.userservice.payload.ApiResponse;
import com.storyart.userservice.payload.PagedResponse;
import com.storyart.userservice.payload.SignUpRequest;
import com.storyart.userservice.payload.UserInManagementResponse;
import com.storyart.userservice.repository.RoleRepository;
import com.storyart.userservice.repository.UserRepository;
import com.storyart.userservice.security.CurrentUser;
import com.storyart.userservice.security.UserPrincipal;
import com.storyart.userservice.service.RoleService;
import com.storyart.userservice.service.UserService;
import com.storyart.userservice.util.AppContants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/systemad")
@CrossOrigin
@Secured({"ROLE_SYSTEM_ADMIN"})
public class SystemAdminController {


    @Autowired
    UserRepository userRepository;


    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<?>
    createAdminAccount(@RequestBody @Valid SignUpRequest signUpRequest) {
        if (userService.findByUsername(signUpRequest.getUsername()) != null) {
            throw new BadRequestException("Tên đăng nhập này đã đã được đăng ký bởi ai đó!");
        }
        if (userService.findByEmail(signUpRequest.getEmail()) != null) {
            throw new BadRequestException("Email này đã được đăng ký bởi ai đó!");
        }
        User user = new User();
        user.setActive(true);
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
//        Role userRole
        //todo : missing role of a user
        Role userRole = roleRepository.findRoleByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new AppException("Hiện giờ chưa thể tạo tài khoản! Vui lòng quay lại sau!"));
        user.setRoleId(userRole.getId());
        User savedUser = userRepository.save(user);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/user/username")
                .buildAndExpand(savedUser.getUsername()).toUri();
        return ResponseEntity.created(location).body(new ApiResponse(true, "Tạo tài khoản thành công!"));
    }

    // todo; not allow admin to set active of sysadmin
    //todo: update data and login with email
//todo: check lai n-n user vs role?!
    @GetMapping("/admins")
    public PagedResponse<UserInManagementResponse>
    getAllAdmin(@RequestParam(value = "page",
            defaultValue = AppContants.DEFAULT_PAGE_NUMBER) int page,
                @RequestParam(value = "size",
                        defaultValue = AppContants.DEFAULT_PAGE_SIZE) int size,
                @RequestParam(value = "s") String searchtxt) {
        return userService.findAdminbyUsernameOrEmail(page, size, searchtxt);
    }

    @Autowired
    RoleService roleService;


    //system admin khong the dung api nay de set status cho no duoc
    @DeleteMapping(value = "/admins/{uid}")
    public ResponseEntity<?>
    setStatusAdmin(@CurrentUser UserPrincipal
                           systemAdmin, @PathVariable("uid") Integer uid, @RequestParam("setActive") boolean setActive) {
        User adminById = userService.findById(uid);
        if (adminById == null) {
            throw new ResourceNotFoundException("id", "User", uid);
        }

        System.out.println("day la role cua admin:"+systemAdmin.getRoleName());


        Role roleById = roleService.findRoleById(adminById.getRoleId());
         //check role xem co phải role khac admin hay khong
        // neu khac thi khong cho set status boi ssytemad không có quyền đó
       if (roleById.getName() != RoleName.ROLE_ADMIN) {
            return new ResponseEntity<>(new ApiResponse(false, "Khóa tài khoản thất bại!"), HttpStatus.BAD_REQUEST);
        }
        if (!setActive) {
            // todo add them ly do deactive cho ca hai
            userService.setStatusByAdmin( false, uid);
            return new ResponseEntity<>(new ApiResponse(false, "Đã khóa tài khoản '"+adminById.getUsername()+"'"), HttpStatus.OK);
        } else {

            userService.setStatusByAdmin(true,uid);
            return new ResponseEntity<>(new ApiResponse(true, "Mở tài khoản '"+adminById.getUsername()+"' thành công!"), HttpStatus.OK);

        }
    }


}
