package com.fptu.swp391.se1839.oemevwarrantymanagement.config;

import java.util.Objects;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.IntrospectRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.UserService;

@Component
public class CustomerJwtDecoder implements JwtDecoder {

    @Value("${SIGN_KEY}")
    private String SIGN_KEY;

    @Autowired
    private UserService userService;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            var response = userService.inprospect(IntrospectRequest.builder()
                    .token(token)
                    .build());
            if (!response.isStatus()) {
                throw new JwtException("Token introspection failed: token is invalid");
            }
        } catch (Exception e) {
            throw new JwtException("Token introspection error: " + e.getMessage(), e);
        }

        if (Objects.isNull(nimbusJwtDecoder)) {
            if (SIGN_KEY == null || SIGN_KEY.isBlank()) {
                throw new IllegalStateException("SIGN_KEY is not configured properly");
            }
            SecretKeySpec secretKeySpec = new SecretKeySpec(SIGN_KEY.getBytes(), "HS512");
            this.nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        try {
            return nimbusJwtDecoder.decode(token);
        } catch (JwtException e) {
            throw new JwtException("Failed to decode JWT: " + e.getMessage(), e);
        }
    }
}
