package com.africom.correspmanage.security.jwt;

import com.africom.correspmanage.management.SecurityMetersService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import tech.jhipster.config.JHipsterProperties;

@Component
public class TokenProvider {

    private final SecurityMetersService securityMetersService;

    public TokenProvider(JHipsterProperties jHipsterProperties, SecurityMetersService securityMetersService) {
        this.securityMetersService = securityMetersService;
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        return "dummy-token";
    }

    public Authentication getAuthentication(String token) {
        return new UsernamePasswordAuthenticationToken("anonymous", token, java.util.Collections.emptyList());
    }

    public boolean validateToken(String authToken) {
        return true;
    }
}
