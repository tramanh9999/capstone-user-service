package com.storyart.userservice.config;

import com.storyart.userservice.security.JwtAuthenticationEntryPoint;
import com.storyart.userservice.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    UserDetailsService userDetailsService;




    // config to let auth know where to load user for matching credentials
    // use BCryptpasswordEncoder
    @Autowired
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());

    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //dont need csrf
        http.
                cors().
                    and().
                csrf().
                    disable().
                exceptionHandling().
                    authenticationEntryPoint(jwtAuthenticationEntryPoint).
                    and().
                sessionManagement().
                    sessionCreationPolicy(SessionCreationPolicy.STATELESS).
                    and().
                //dont authenticate this paticulat request
                authorizeRequests().
                    antMatchers("/",
                        "/favicon.ico",
                        "/**/*.png",
                        "/**/*.gif",
                        "/**/*.svg",
                        "/**/*.jpg",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js").
//todo add more permit api in here
                        permitAll().
                antMatchers("/api/v1/auth/**").
                    permitAll().
                antMatchers("/api/v1/user/checkingUserAvailability",
                        "/api/v1/user/public_profile").
                    permitAll().
                antMatchers(HttpMethod.POST,"/api/v1/auth/signup").permitAll().
                antMatchers("/api/v1/forgot-password").permitAll().
                antMatchers("/api/v1/reset-password").permitAll().
                antMatchers(HttpMethod.GET,"/api/v1/reset-password/checkToken/**").permitAll().
                antMatchers("/api/v2/api-docs").permitAll().
                antMatchers("/api/swagger-ui.html").permitAll().


                antMatchers(HttpMethod.GET,"/api/v1/user/**").permitAll()
                // all other reqs need to be authenticated
                .anyRequest().
                    authenticated();
        // using stateless session , session wont be used to store user state
        ;

// Add filter to validate the tokens with every request
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    }


}
