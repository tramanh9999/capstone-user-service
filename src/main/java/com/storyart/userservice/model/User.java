package com.storyart.userservice.model;

import com.storyart.userservice.common.DateAudit;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Calendar;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user",
        uniqueConstraints = {@UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")})
public class User extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    
    @Size(min = 3, max = 15, message = "Tên đăng nhập phải có từ 3 đến 15 ký tự")
    @NotBlank(message = "Tên đăng nhập phải có từ 3 đến 15 ký tự")
    @Column(unique = true)
    private String username;

    
    @Size(max = 40, min = 3, message = "Tên phải có từ 3 đến 40 ký tự")
    @NotBlank(message = "Tên phải có từ 3 đến 40 ký tự")
    @Column(length = 40)
    private String name;

    
    //size 100 is encoded password,, signup request has passord <=15
    @Size(max = 100, min = 8, message = "Mật khẩu phải có từ 8 đến 100 ký tự")
    @NotBlank(message = "Mật khẩu phải có từ 8 đến 100 ký tự")
    private String password;

    @Size(max = 1000, message = "")
    private String avatar;

    @Size(max = 1000, message = "")
    private String profileImage;

    private int roleId;

    @Size(max = 300, message = "Thông tin giới thiệu có độ dài tối đa là 300 ký tự!")
    @Column(length = 300)
    private String introContent;
    private boolean isActive; 

    @Email(message = "Email không đúng định dạng. Vui lòng nhập lại email")
    @NotBlank(message = "Email không được để trống")
    private String email;

    private Date createdAt;

    private Date updatedAt;

    boolean isDeactiveByAdmin;
    private String token;
    Date expiryDate;

    public void setExpiryDate(int minutes) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, minutes);
        this.expiryDate = now.getTime();
    }

    public boolean isExpired() {
        return new Date().after(this.expiryDate);
    }


    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }


    public void setUsername(String username) {
        this.username = username.trim();
    }

    public void setName(String name) {
        this.name = name.trim();
    }



    public void setIntroContent(String introContent) {
        this.introContent = introContent.trim();
    }

    public void setEmail(String email) {
        this.email = email.trim();
    }
}
