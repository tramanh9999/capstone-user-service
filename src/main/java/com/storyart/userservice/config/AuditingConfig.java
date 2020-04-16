package com.storyart.userservice.config;


import com.storyart.userservice.security.UserPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
/*automatically populate the createdBy and updatedBy fields, we need to make the following modifications to the
  AuditingConfig class -*/
public class AuditingConfig {


    @Bean
    public AuditorAware<Integer> auditorProvider(){
        return new SpringSecurityAwareImpl();
    }



     class SpringSecurityAwareImpl implements AuditorAware<Integer> {


         @Override
         public Optional<Integer> getCurrentAuditor() {

             Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
             if( authentication== null ||
                 !authentication.isAuthenticated()|| authentication instanceof AnonymousAuthenticationToken){
                 return Optional.empty();
             }
             UserPrincipal userPrincipal= (UserPrincipal) authentication.getPrincipal();

             return Optional.ofNullable(userPrincipal.getId());

         }

     }
}
