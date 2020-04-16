package com.storyart.userservice.controller;


import com.storyart.userservice.common.constants.RoleName;
import com.storyart.userservice.exception.AppException;
import com.storyart.userservice.exception.BadRequestException;
import com.storyart.userservice.exception.ResourceNotFoundException;
import com.storyart.userservice.exception.UnauthorizedException;
import com.storyart.userservice.model.Role;
import com.storyart.userservice.model.User;
import com.storyart.userservice.payload.ApiResponse;
import com.storyart.userservice.payload.PagedResponse;
import com.storyart.userservice.payload.SignUpRequest;
import com.storyart.userservice.payload.UserInManagementResponse;
import com.storyart.userservice.repository.RoleRepository;
import com.storyart.userservice.repository.UserRepository;
import com.storyart.userservice.security.JwtTokenProvider;
import com.storyart.userservice.service.RoleService;
import com.storyart.userservice.service.UserService;
import com.storyart.userservice.util.AppContants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin("*")
@Secured({"ROLE_ADMIN"})
public class AdminController {


    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;


    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    RoleService roleService;
    // todo missing check available user (is active or not api)
    /* Deactivating a user without checking it deactived or not */
    @DeleteMapping(value = "/users/{uid}")
    public ResponseEntity<?>
    setStatusUser(@PathVariable Integer uid,
                  @RequestParam(value = "setActive") boolean setActive) {

        User userById = userService.findById(uid);
        if (userById == null) {
            throw new ResourceNotFoundException("id", "User", uid);
        }
        Role roleById = roleService.findRoleById(userById.getRoleId());
        //check role xem co phải role khac admin hay khong
        // neu khac thi khong cho set status boi ssytemad không có quyền đó
        if (roleById.getName() != RoleName.ROLE_USER) {
            throw new UnauthorizedException("Bạn không thể chỉnh sửa nội dung này!");
        }
        if (!setActive) {
            userService.setStatusByAdmin( false,uid);
        } else {
            userService.setStatusByAdmin(true,uid);
        }
        User byId = userService.findById(uid);
        boolean status = byId.isDeactiveByAdmin();

        //todo thieu set deactive by admin account record
        if (!status) {
            return new ResponseEntity<>(new ApiResponse(true, "Mở tài khoản '"+byId.getUsername()+"' thành công!"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse(true, "Đã khóa tài khoản '"+byId.getUsername()+"'"), HttpStatus.OK);
        }

    }

    // search user by username contains %username%
    @GetMapping("/users/all")
    public PagedResponse<UserInManagementResponse>
    findAll(@RequestParam(value = "page",
            defaultValue = AppContants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size",

                    defaultValue = AppContants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(value = "s") String searchtxt) {

        return userService.findByUsernameOrEmail(page, size, searchtxt);
    }

    @GetMapping("/users/userOnly")
    public PagedResponse<UserInManagementResponse>
    findOnlyUserByUsernameLike(@RequestParam(value = "page",
            defaultValue = AppContants.DEFAULT_PAGE_NUMBER) int page,
                               @RequestParam(value = "size",

                                       defaultValue = AppContants.DEFAULT_PAGE_SIZE) int size,
                               @RequestParam(value = "s") String searchtxt) {

        return userService.findOnlyUserByUsernameOrEmail(page, size, searchtxt);
    }

    //todo: cannot able to active account -that deactived by sys/admin- 2 functions


    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/users/add")
    public ResponseEntity<?>
    createUser(@RequestBody
              @Valid SignUpRequest signUpRequest) {
        if (userService.findByUsername(signUpRequest.getUsername()) != null) {
            throw new BadRequestException("Tên đăng nhập  này đã  được đăng ký bơi người khác");
        }

        if (userService.findByEmail(signUpRequest.getEmail()) != null) {

            throw new BadRequestException("Email này đã được đăng ký bơi người khác");

        }

        User user = new User();
        user.setActive(true);
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setIntroContent(signUpRequest.getIntro_content());

//        Role userRole
        Role userRole = roleRepository.findRoleByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("Hiện giờ chưa thể tạo tài khoản! Vui lòng quay lại sau!"));

        user.setRoleId(userRole.getId());


        /**
         *  Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
         *                 .orElseThrow(() -> new AppException("User Role not set."));
         *
         *         user.setRoles(Collections.singleton(userRole));*/


        User savedUser = userRepository.save(user);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/user/username")
                .buildAndExpand(savedUser.getUsername()).toUri();
        return ResponseEntity.created(location).body(new ApiResponse(true, "Tạo tài khoản thành công!"));

    }
}
