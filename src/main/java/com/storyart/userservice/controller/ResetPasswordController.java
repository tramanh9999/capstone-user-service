package com.storyart.userservice.controller;

import com.netflix.ribbon.proxy.annotation.Http;
import com.storyart.userservice.exception.BadRequestException;
import com.storyart.userservice.exception.ResourceNotFoundException;
import com.storyart.userservice.model.User;
import com.storyart.userservice.payload.ApiResponse;
import com.storyart.userservice.payload.JwtAuthenticationResponse;
import com.storyart.userservice.payload.PasswordChangeRequest;
import com.storyart.userservice.payload.ResetPasswordRequest;
import com.storyart.userservice.repository.UserRepository;
import com.storyart.userservice.security.JwtTokenProvider;
import com.storyart.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/reset-password")
public class ResetPasswordController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @GetMapping("checkToken/{token}")
    ResponseEntity checkToken(@PathVariable("token") String token) {
        User user = userRepository.findByToken(token);
        if (user == null) {
            throw new BadRequestException("Không thể đặt lại mật khẩu");
        }
        if (user.isExpired()) {
            throw new BadRequestException("Yêu cầu đặt lại mật khẩu đã hết hạn, vui lòng gửi lại");
        }
        return new ResponseEntity(new ApiResponse(true, "Token hợp lệ"), HttpStatus.OK);
    }


    @PostMapping
    @Transactional
    public ResponseEntity handlePasswordReset(@RequestBody @Valid ResetPasswordRequest form
    ) {

        User user = userRepository.findByToken(form.getToken());
        if (user == null) {
            throw new ResourceNotFoundException("Người dùng", "mã đặt lại mật khẩu", form.getToken());
        }
        if (user.isExpired()) {
            throw new BadRequestException("Yêu cầu đặt lại mật khẩu đã hết hạn, vui lòng gửi lại");
        }


        if (!form.getPassword().equals(form.getRepassword())) {
            throw new BadRequestException("Mật khẩu nhập lại không đúng. Vui lòng nhâp lại!");
        }
        User token = userRepository.findByToken(form.getToken());
        String updatedPassword = passwordEncoder.encode(form.getPassword());
        token.setPassword(updatedPassword);
        token.setToken(null);

        Authentication authentication;

        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        form.getPassword()
                )
        );
        String jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }


}
