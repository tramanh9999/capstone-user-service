package com.storyart.userservice.controller;

import com.storyart.userservice.common.constants.RoleName;
import com.storyart.userservice.exception.AppException;
import com.storyart.userservice.exception.BadRequestException;
import com.storyart.userservice.model.Role;
import com.storyart.userservice.model.User;
import com.storyart.userservice.payload.SignUpRequest;
import com.storyart.userservice.repository.RoleRepository;
import com.storyart.userservice.repository.UserRepository;
import com.storyart.userservice.security.JwtTokenProvider;
import com.storyart.userservice.payload.ApiResponse;
import com.storyart.userservice.payload.JwtAuthenticationResponse;
import com.storyart.userservice.payload.LoginRequest;
import com.storyart.userservice.security.JwtUserDetailsService;
import com.storyart.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Calendar;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/auth")
public class JwtAuthenticationController {


    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    JwtUserDetailsService userDetailsService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserService userService;

    @Autowired
    JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;
    /**
     * above is demo
     * 10:57 PM 27/2/2020 add sign in an sign up api
     */

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication;

        try {
          authentication  = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Sai tên đăng nhập hoặc mật khẩu!");
        }
        User us= userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
        if(us!= null && us.isDeactiveByAdmin()){
            throw new BadRequestException("Tài khoản đã bị khóa. Vui lòng liên hệ với quản trị viên!");
        }



        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }



    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (userService.findByUsername(signUpRequest.getUsername()) != null) {
            throw new BadCredentialsException("Tên đăng nhập  này đã được đăng ký bởi ai đó!");
        }
        if (userService.findByEmail(signUpRequest.getEmail()) != null) {
            throw new BadRequestException("Email này đã được đăng ký bởi người khác");

        }
        User user = new User();
        user.setActive(true);
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setIntroContent(signUpRequest.getIntro_content());
        user.setAvatar(signUpRequest.getAvatar());

//        Role userRole
        Role userRole = roleRepository.findRoleByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("Hiện tại chưa thể đăng ký! Vui lòng quay lại sau!"));
        user.setRoleId(userRole.getId());


        /**
         *  Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
         *                 .orElseThrow(() -> new AppException("User Role not set."));
         *
         *         user.setRoles(Collections.singleton(userRole));*/

        User savedUser = userRepository.save(user);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/user/username")
                .buildAndExpand(savedUser.getUsername()).toUri();
        return ResponseEntity.created(location).body(new ApiResponse(true, "Đăng ký thành công!"));

    }


}
