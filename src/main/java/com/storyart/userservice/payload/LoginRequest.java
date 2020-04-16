package com.storyart.userservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @Size(min = 3, max = 15, message = "Tên đăng nhập phải có từ 3 đến 15 ký tự")
    @NotBlank(message = "Tên đăng nhập phải có từ 3 đến 15 ký tự")
    private String username;


    @Size(max = 100, min = 8, message = "Mật khẩu phải có từ 8 đến 100 ký tự")
    @NotBlank(message = "Mật khẩu phải có từ 8 đến 100 ký tự")
    private String password;

    public void setUsername(String username) {
        this.username = username.trim();
    }

    public void setPassword(String password) {
        this.password = password.trim();
    }
}
