package com.storyart.userservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailSubmitRequest {
    @Email(message = "Không đúng định dạng email!")
    @NotBlank(message = "Email không được để trống!")
    String email;

    String linkReset;


}
