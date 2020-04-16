package com.storyart.userservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateRequest {
    @Size(max = 40, min = 3, message = "Tên phải có từ 3 đến 40 ký tự")
    @NotBlank(message = "Tên phải có từ 3 đến 40 ký tự")
    private String name;
    @Email(message = "Email không đúng định dạng. Vui lòng nhập lại email")
    @NotBlank(message = "Email không được để trống")
    @NaturalId
  private   String email;

    @Size(min = 3, max = 15, message = "Tên đăng nhập phải có từ 3 đến 15 ký tự")
    @NotBlank(message = "Tên đăng nhập phải có từ 3 đến 15 ký tự")
    @Column(unique = true)
    private String username;
    @Size(max = 300, message = "Thông tin giới thiệu có độ dài tối đa là 300 ký tự!")
   private String intro_content;

    public void setName(String name) {
        this.name = name.trim();
    }

    public void setEmail(String email) {
        this.email = email.trim();
    }

    public void setUsername(String username) {
        this.username = username.trim();
    }

    public void setIntro_content(String intro_content) {
        this.intro_content = intro_content.trim();
    }
}
