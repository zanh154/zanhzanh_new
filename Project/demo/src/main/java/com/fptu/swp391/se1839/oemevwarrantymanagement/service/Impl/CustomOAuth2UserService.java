// package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

// import java.util.Map;

// import org.springframework.beans.factory.annotation.Autowired;
// import
// org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
// import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
// import
// org.springframework.security.oauth2.core.OAuth2AuthenticationException;
// import org.springframework.security.oauth2.core.user.OAuth2User;
// import org.springframework.stereotype.Service;

// import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.User;
// import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.User.Role;
// import
// com.fptu.swp391.se1839.oemevwarrantymanagement.repository.UserRepository;

// @Service

// public class CustomOAuth2UserService extends DefaultOAuth2UserService {

// @Autowired
// private UserRepository userRepository;

// @Override
// public OAuth2User loadUser(OAuth2UserRequest userRequest) throws
// OAuth2AuthenticationException {
// OAuth2User oAuth2User = super.loadUser(userRequest);

// Map<String, Object> attributes = oAuth2User.getAttributes();
// String email = (String) attributes.get("email");
// String name = (String) attributes.get("name");
// String picture = (String) attributes.get("picture");

// // Tìm user theo email
// User user = userRepository.findByEmail(email)
// .orElseGet(() -> {
// // Nếu chưa có thì tạo mới
// User newUser = new User();
// newUser.setEmail(email);
// newUser.setName(name);
// newUser.setAvatar(picture);
// newUser.setProvider("GOOGLE"); // ← Đánh dấu là Google user
// newUser.setPassword(null); // ← Không cần password
// newUser.setStatus(User.Status.ACTIVE);
// newUser.setRequiresPasswordChange(false);
// newUser.setRole(Role.);
// return userRepository.save(newUser);
// });

// // Nếu user đã tồn tại nhưng đăng ký bằng LOCAL, update provider
// if ("LOCAL".equals(user.getProvider())) {
// user.setProvider("GOOGLE");
// user.setAvatar(picture);
// userRepository.save(user);
// }

// return oAuth2User;
// }
// }