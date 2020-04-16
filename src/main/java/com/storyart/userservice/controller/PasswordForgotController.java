package com.storyart.userservice.controller;

import com.storyart.userservice.exception.BadRequestException;
import com.storyart.userservice.model.User;
import com.storyart.userservice.payload.ApiResponse;
import com.storyart.userservice.payload.EmailSubmitRequest;
import com.storyart.userservice.payload.EmailSender;
import com.storyart.userservice.repository.UserRepository;
import com.storyart.userservice.service.EmailService;
import com.storyart.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/forgot-password")
@CrossOrigin
public class PasswordForgotController {


    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Value("${password.expiryMinutes}")
    int minutes;

    @Autowired
    HttpServletRequest request;



    @PostMapping
    public ResponseEntity<?> processForgotPasswordForm(@RequestBody @Valid EmailSubmitRequest form) {

        User user = userService.findByEmail(form.getEmail());
        if (user == null) {
            throw new BadRequestException("Không tồn tại người dùng với email này!");
        }

//    PasswordResetToken token = new PasswordResetToken();
        user.setToken(UUID.randomUUID().toString());
        user.setExpiryDate(minutes);
        //update token and expiry time
        userRepository.save(user);

        EmailSender mail = new EmailSender();
        mail.setFrom("no-reply@storyart.com");
        mail.setTo(user.getEmail());
        mail.setSubject("Đặt lại mật khẩu");

        Map<String, Object> model = new HashMap<>();
        model.put("user", user);
        model.put("expiry", minutes);
        model.put("signature", "Story Art TeaM");


//        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        model.put("resetUrl", form.getLinkReset()+"/token/"+user.getToken());
        mail.setModel(model);
        emailService.sendEmail(mail);

        return new ResponseEntity(new ApiResponse(true, "Vui lòng kiểm tra email, yêu cầu tạo lại mật khẩu đã được gửi"), HttpStatus.OK);
    }





}
