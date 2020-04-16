package com.storyart.userservice.model;

import com.storyart.userservice.common.DateAudit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "screen")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Screen extends DateAudit {

//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(length = 100)
    @Id
    private String id;

    private int storyId;

    @Size(max = 100)
    private String title;

    @Column(length = 10000)
    @Size(min = 10)
    @NotBlank
    private String content;

    private String nextScreenId;

}
