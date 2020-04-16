package com.storyart.userservice.security;


import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

//todo component scan qua day hay ko?
//todo ky hieu ::
@Component
public class JwtTokenProvider implements Serializable {

    @Value("${jwt.secret}")
    private String jwtSecret;


    @Value("${jwt.expirationInMs}")
    private int jwtExpirationInMs;

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal)
                authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        return Jwts.builder()
                .setSubject(Integer.toString(userPrincipal.getId()))
                .claim("id", userPrincipal.getId())
                .claim("username", userPrincipal.getUsername())
                .claim("name", userPrincipal.getName())
                .claim("role", userPrincipal.getAuthorities().toArray()[0].toString())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    public Integer getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return Integer.parseInt(claims.getSubject());
    }

    /*when user pass login , attach a token generated (server gave it from his username and password) into header
    , sending that token to access any resouce he can access by his role */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Sai cú pháp token!");
        } catch (MalformedJwtException ex) {
            logger.error("Token không đúng");
        } catch (ExpiredJwtException ex) {
            logger.error("Token đã hết hạn");
        } catch (UnsupportedJwtException ex) {
            logger.error("Không hỗ trợ jwt token");
        } catch (IllegalArgumentException ex) {
            logger.error("Chuối token chứa claim không hợp lệ");
        }
        return false;
    }

}
