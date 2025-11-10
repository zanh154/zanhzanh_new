package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.User;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.UserRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

        @Autowired
        UserService userService;

        @Autowired
        UserRepository userRepository;

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                        Authentication authentication) throws IOException, ServletException {

                OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                String email = oAuth2User.getAttribute("email");

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                String token = this.userService.generateToken(user);

                String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/auth/callback")
                                .queryParam("token", token)
                                .queryParam("email", user.getEmail())
                                .queryParam("name", user.getName())
                                .queryParam("role", user.getRole().name())
                                .build()
                                .toUriString();

                getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
}
