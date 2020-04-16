package com.storyart.userservice;

import com.storyart.userservice.service.RoleService;
import com.storyart.userservice.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.annotation.PostConstruct;
import java.util.TimeZone;


@SpringBootApplication
//@EnableFeignClients
@EnableJpaRepositories("com.storyart.userservice.repository")
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

//    @PostConstruct
//    void init() {
//        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
//    }


    @Bean
    JavaMailSender javaMailSender(){
        return new JavaMailSenderImpl();
    }

    @Bean
    public CommandLineRunner createTestStories(RoleService roleService, UserService userService){
        return args -> {
//            roleService.createDefaultRoles();
//            userService.createDefaultSysAdmin();
        };
    }
}
