package com.storyart.userservice.payload;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "Tên đăng nhập không được trống")
    @Size(min=3,max = 15, message = "Tên đăng nhập phải có từ 3 đến 15 ký tự")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 40, min = 3, message = "Tên phải có từ 3 đến 40 ký tự")
    @Column(length = 40)
    private String name;

    @Email(message = "Email không đúng định dạng!")
    @NaturalId
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    //size 100 is encoded password,, signup request has passord <=15
    @Size(max = 100, min = 8, message = "Mật khẩu phải có từ 8 đến 100 ký tự")
    private String password;


    @Size(max = 300, message = "Thông tin giới thiệu có độ dài tối đa là 300 ký tự")
    String intro_content;


    String avatar;


}
