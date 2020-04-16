package com.storyart.userservice.service;


import com.storyart.userservice.payload.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.templateresource.ServletContextTemplateResource;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public interface EmailService {

    void sendEmail(EmailSender mail);
}

@Service
class EmailServiceImpl implements EmailService {


//    @Autowired
//    private JavaMailSender emailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;
    @Value("${spring.mail.properties.mail.smtp.auth}")
    String auth;
    @Value("${spring.mail.properties.mail.smtp.connectiontimeout}")
    String connectTimeout;
    @Value("${spring.mail.properties.mail.smtp.timeout}")
    String timeout;
    @Value("${spring.mail.properties.mail.smtp.writetimeout}")
    String writeTimeout;
    @Value("${spring.mail.protocol}")
    String protocol;
    @Value("${spring.mail.test-connection}")
    String testConnection;
    @Value("${spring.mail.host}")
    String host;
    @Value("${spring.mail.port}")
    String port;
    @Value("${spring.mail.username}")
    String username;
    @Value("${spring.mail.password}")
    String password;
    @Value("${spring.mail.default-encoding}")
    String defEncode;
    @Value("${spring.mail.smtp.starttls.enable}")
    String starttls;

    @Autowired
    ClassLoaderTemplateResolver classLoaderTemplateResolver;

    public void sendEmail(EmailSender mail) {
        try {
            System.out.println("username, password: |"+ username+","+password+"|");
            Properties props = new Properties();
            props.put("mail.smtp.starttls.enable",starttls );
            props.put("mail.smtp.host", host);
            props.put("mail.port", port);
            props.put("mail.protocol", protocol);
            props.put("mail.smtp.auth", auth);
            props.put("mail.default-encoding", defEncode);
            props.put("mail.test-connection", testConnection);
            props.put("mail.properties.mail.smtp.writetimeout", writeTimeout);
            props.put("mail.properties.mail.smtp.timeout", timeout);
            props.put("mail.properties.mail.smtp.connectiontimeout", connectTimeout);

            Session session = Session.getDefaultInstance(props, new Authenticator() {
              @Override
                protected  PasswordAuthentication getPasswordAuthentication(){
                  return new PasswordAuthentication(username, password);
              }
            });


//            MimeMessage message = emailSender.createMimeMessage();
            MimeMessage message = new MimeMessage(session);
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            System.out.println("prefix" + classLoaderTemplateResolver.getPrefix());

            Context context = new Context();
            context.setVariables(mail.getModel());


            String html = templateEngine.process("email-template", context);

            helper.setTo(mail.getTo());
            helper.setText(html, true);
            helper.setSubject(mail.getSubject());
            helper.setFrom(mail.getFrom());

            Transport.send(message);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
