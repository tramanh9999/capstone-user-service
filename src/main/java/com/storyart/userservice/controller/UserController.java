package com.storyart.userservice.controller;

import com.storyart.userservice.dto.ResultDto;
import com.storyart.userservice.exception.BadRequestException;
import com.storyart.userservice.exception.ResourceNotFoundException;
import com.storyart.userservice.exception.UnauthorizedException;
import com.storyart.userservice.model.User;
import com.storyart.userservice.payload.*;
import com.storyart.userservice.security.CurrentUser;
import com.storyart.userservice.security.UserPrincipal;
import com.storyart.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * ref from usercontroller
 */


@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/{uid}")
    public UserProfileResponse get(@PathVariable("uid") Integer uid) {
        User user = userService.findById(uid);
        UserProfileResponse userProfileResponse = new UserProfileResponse();

        if (user == null) {
            throw new ResourceNotFoundException("User", "id", uid);
        } else {
            userProfileResponse.setName(user.getName());
            userProfileResponse.setUsername(user.getUsername());
            userProfileResponse.setEmail(user.getEmail());
            userProfileResponse.setActive(user.isActive());
            userProfileResponse.setJointAt(user.getCreatedAt());
            userProfileResponse.setAvatar(user.getAvatar());
        }
        return userProfileResponse;

    }

    @GetMapping("current")
    public ResponseEntity getCurrentUser(@CurrentUser UserPrincipal userPrincipal){
        ResultDto result = userService.getUserProfile(userPrincipal.getId());
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping(value = "/username/{username}")
    public UserProfileResponse
    findByUsername(@PathVariable("username") String username) {
        User user = userService.findByUsername(username);
        UserProfileResponse userProfileResponse = new UserProfileResponse();

        if (user == null) {
            throw new ResourceNotFoundException("User", "username", username);
        } else {
            userProfileResponse = new UserProfileResponse(user);
        }
        return userProfileResponse;
    }

    @GetMapping(value = "/me")
    public UserProfileResponse currentUser(@CurrentUser UserPrincipal userPrincipal) {
        User user = userService.findByUsername(userPrincipal.getUsername());
        UserProfileResponse userProfileResponse = new UserProfileResponse();

        if (user == null) {
            throw new ResourceNotFoundException("User", "username", userPrincipal.getUsername());
        } else {
            userProfileResponse = new UserProfileResponse(user);
        }
        return userProfileResponse;
    }

    @GetMapping(value = "public_profile/{userId}")
    public ResponseEntity currentUser(@PathVariable int userId) {
        ResultDto result = userService.getUserProfile(userId);
       return new ResponseEntity(result, HttpStatus.OK);
    }

    @PutMapping(value = "/{uid}")
    public UserProfileResponse
    update(@PathVariable("uid") Integer uid,
           @RequestBody UserProfileUpdateRequest user, @CurrentUser UserPrincipal userPrincipal) {

        if (userPrincipal.getId() != uid) {
            throw new UnauthorizedException("Bạn không thể chỉnh sửa nội dung này!");
        }
        //nếu tìm được user khác user hiện tại, có email trùng thì báo lõi trùng email
        User user1 = userService.findByEmail(user.getEmail());
        if (user1 != null) {
            if (user1.getId() != userPrincipal.getId()) {
                throw new BadRequestException("Email đã được đăng ký bởi ai đó!");
            }
        }
        User userServiceByUsername = userService.findByUsername(user.getUsername());

        if (userServiceByUsername != null) {
            if (userServiceByUsername.getId() != userPrincipal.getId()) {
                throw new BadRequestException("Tên đăng nhập đã tồn tại!");
            }
        }
        User userById = userService.findById(uid);
        if (userById == null) {
            throw new ResourceNotFoundException("User", "id", uid);
        }
        if (userPrincipal.getUsername().equals(userById.getUsername())) {
            userService.update(uid, user);
        }


        UserProfileResponse us = new UserProfileResponse(userService.findById(uid));
        return us;
    }

    // todo missing check available user (is active or not api)
    /* Deactivating a user without checking it deactived or not */
    @DeleteMapping(value = "/{uid}")
    public ResponseEntity<?>
    setStatusYourAccount(@PathVariable Integer uid, @CurrentUser UserPrincipal currentUser,
                         @Param(value = "setActive") boolean setActive) {
        //this user must being active and param: setActive=false
        if (currentUser.getId() != uid) {
            throw new UnauthorizedException("Bạn không thể chỉnh sửa nội dung này!");
        }
        // trong th tu khoa tai khoan khi tai khoan đa khoa boi admin!
        if (userService.findById(currentUser.getId()).isDeactiveByAdmin()) {
            return new ResponseEntity<>(new ApiResponse(false, "Tài khoản đã khóa bởi quản trị viên!"), HttpStatus.FORBIDDEN);
        }

        if (!setActive) {
            userService.setStatus(false ,uid );
            return new ResponseEntity<>(new ApiResponse(true, "Đã khóa tài khoản!"), HttpStatus.OK);
        } else {
            //user khong the tu mo tai khoan bi khoa boi admin
            if (userService.findById(currentUser.getId()).isDeactiveByAdmin()) {
                return new ResponseEntity<>(new ApiResponse(false, "Không thể mở tài khoản! Vui lòng liên lạc với quản trị viên!"), HttpStatus.FORBIDDEN);
            } else {
                userService.setStatus(true,uid);
                return new ResponseEntity<>(new ApiResponse(true, "Mở khóa tài khoản thành công!"), HttpStatus.OK);
            }
        }
    }

    @PostMapping(value = "/{uid}/avatar/save")
    public ResponseEntity<?> saveAvatarLink(@PathVariable("uid") Integer uid, @CurrentUser UserPrincipal userPrincipal, @RequestBody AvatarUpdateRequest avatarUpdateRequest) {
        if (userPrincipal.getId() != uid) {
            throw new UnauthorizedException("Bạn không thể chỉnh sửa nội dung này!");
        }
        userService.updateAvatar(uid, avatarUpdateRequest.getLink());
        return new ResponseEntity<>(new ApiResponse(true, "Lưu thành công!"), HttpStatus.OK);
    }

    @PostMapping(value = "/{uid}/profileImage/save")
    public ResponseEntity<?> saveProfileImageLink(@PathVariable("uid") Integer uid, @CurrentUser UserPrincipal userPrincipal, @RequestBody AvatarUpdateRequest avatarUpdateRequest) {
        if (userPrincipal.getId() != uid) {
            throw new UnauthorizedException("Bạn không thể chỉnh sửa nội dung này!");
        }
        userService.updateProfileImage(uid, avatarUpdateRequest.getLink());
        return new ResponseEntity<>(new ApiResponse(true, "Lưu thành công!"), HttpStatus.OK);
    }

    @PostMapping(value="/{uid}/password")
    public ResponseEntity<?> changePassword(@PathVariable("uid") int userId,@Valid  @RequestBody PasswordChangeRequest passwordChangeRequest){

if(!passwordChangeRequest.getPassword().equals(passwordChangeRequest.getRepassword())){
    throw new BadRequestException("Mật khẩu nhập lại không đúng. Vui lòng nhập lại!");
}

       boolean isChanged= userService.changePassword(passwordChangeRequest.getPassword(), userId);



        return isChanged? new ResponseEntity<>(new ApiResponse(true, "Đổi mật khẩu thành công!"), HttpStatus.OK):new ResponseEntity<>(new ApiResponse(false, "Đổi mật khẩu thất bại thử lại!"), HttpStatus.BAD_REQUEST) ;

    }



//
//    @PostMapping("forgot-password")
//    public String processForgotPasswordForm(@ModelAttribute("forgotPasswordForm") @Valid PasswordForgotDto form,
//                                            BindingResult result,
//                                            HttpServletRequest request) {
//
//        if (result.hasErrors()) {
//            return "forgot-password";
//        }
//
//        User user = userService.findByEmail(form.getEmail());
//        if (user == null) {
//            result.rejectValue("email", null, "We could not find an account for that e-mail address.");
//            return "forgot-password";
//        }
//
//        PasswordResetToken token = new PasswordResetToken();
//        token.setToken(UUID.randomUUID().toString());
//        token.setUser(user);
//        token.setExpiryDate(30);
//        tokenRepository.save(token);
//
//        Mail mail = new Mail();
//        mail.setFrom("no-reply@memorynotfound.com");
//        mail.setTo(user.getEmail());
//        mail.setSubject("Password reset request");
//
//        Map<String, Object> model = new HashMap<>();
//        model.put("token", token);
//        model.put("user", user);
//        model.put("signature", "https://memorynotfound.com");
//        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
//        model.put("resetUrl", url + "/reset-password?token=" + token.getToken());
//        mail.setModel(model);
//        emailService.sendEmail(mail);
//
//        return "redirect:/forgot-password?success";
//    }

}
