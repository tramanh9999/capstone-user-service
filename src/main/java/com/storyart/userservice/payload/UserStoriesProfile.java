package com.storyart.userservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserStoriesProfile {
    private Integer id;
    private String name;
    private String email;
    private Date jointAt;
    private Date modifiedAt;
    private  String introContent;

}
