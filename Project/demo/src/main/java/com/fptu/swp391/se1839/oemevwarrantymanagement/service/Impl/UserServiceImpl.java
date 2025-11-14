package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import com.fptu.swp391.se1839.oemevwarrantymanagement.Utilities.PasswordGeneration;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.ChangePasswordRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.EmailDetailsRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.ForgotPasswordRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.IntrospectRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.LoginRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.LogoutRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.OtpRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.RefeshTokenRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.UserCreateRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.UserSearchRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.UserUpdateRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.IntrospectResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.LoginResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.OTPResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.TechnicianPerformanceResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.UserResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.InvalidToken;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairOrder;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.ServiceCenter;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.User;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.InvalidTokenRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RepairOrderRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.ServiceCenterRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.UserRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.EmailService;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    final UserRepository userRepository;
    final PasswordEncoder passwordEncoder;
    final InvalidTokenRepository invalidTokenRepository;
    final ServiceCenterRepository serviceCenterRepository;
    final OtpServiceImpl otpService;
    final PasswordGeneration passwordGeneration;
    final EmailService emailService;
    final RepairOrderRepository repairOrderRepository;
    @Value("${app.frontend.login-url}")
    String loginUrl;

    private Long getServiceCenterIdSafe(User user) {
        return user.getServiceCenter() != null ? user.getServiceCenter().getId() : null;
    }

    @Override
    public User handleFindByEmailOrPhone(String input) {
        return this.userRepository.findByEmailOrPhoneNumber(input, input)
                .orElseThrow(() -> new NoSuchElementException("Email or Phone isn't correct"));
    }

    @Value("${SIGN_KEY}")
    String SIGN_KEY;

    public LoginResponse authenticate(LoginRequest request) {
        User user = userRepository.findByEmailOrPhoneNumber(request.getUser(), request.getUser())
                .orElseThrow(() -> new NoSuchElementException("Email or Phone isn't correct"));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new IllegalArgumentException("Password isn't correct");
        }

        user.setWorkStatus(User.WorkStatus.AVAILABLE);
        user.setDate(LocalDate.now());

        userRepository.save(user);

        // otpService.sendOtp(user);
        // return LoginResponse.builder()
        // .message("OTP sent to your email/phone. Please verify.")
        // .build();
        String token = generateToken(user);
        return LoginResponse.builder()
                .token(token)
                .status(true)
                .build();
    }

    public OTPResponse handleVerifyOTP(OtpRequest request) {
        boolean valid = otpService.verifyOtp(request.getEmailOrPhoneNumber(), request.getVerify());
        if (valid) {
            User user = userRepository.findByEmail(request.getEmailOrPhoneNumber())
                    .orElseGet(() -> userRepository.findByPhoneNumber(request.getEmailOrPhoneNumber())
                            .get());

            String token = generateToken(user);
            return OTPResponse.builder()
                    .token(token)
                    .message("Verify OTP successfully")
                    .status(true)
                    .name(user.getName())
                    .build();
        }
        return null;
    }

    public IntrospectResponse inprospect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();

        boolean verify = true;
        try {
            verifyToken(token);
        } catch (JOSEException | ParseException e) {
            verify = false;

        }
        return IntrospectResponse.builder()
                .status(verify)
                .build();
    }

    public OTPResponse handleRefeshToken(RefeshTokenRequest request) throws JOSEException, ParseException {
        var verifyToken = verifyToken(request.getToken());

        String jwt = verifyToken.getJWTClaimsSet().getJWTID();
        Date expiDate = verifyToken.getJWTClaimsSet().getExpirationTime();

        User user = userRepository.findByEmailOrPhoneNumber(
                verifyToken.getJWTClaimsSet().getSubject(),
                verifyToken.getJWTClaimsSet().getSubject())
                .orElseThrow(() -> new NoSuchElementException("User is invalid"));

        InvalidToken invalidToken = InvalidToken.builder()
                .id(jwt)
                .expiryDate(expiDate)
                .user(user)
                .build();

        invalidTokenRepository.save(invalidToken);

        String token = generateToken(user);

        return OTPResponse.builder()
                .token(token)
                .status(true)
                .build();
    }

    public String generateToken(User user) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                .subject(user.getEmail() != null ? user.getEmail() : user.getPhoneNumber())
                .issuer("devteria.com")
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plus(8, ChronoUnit.HOURS)))
                .claim("name", user.getName())
                .claim("userId", user.getId())
                .claim("phone", user.getPhoneNumber())
                .claim("role", user.getRole())
                .claim("requiresPasswordChange", user.isRequiresPasswordChange())
                .jwtID(UUID.randomUUID().toString());

        if (user.getServiceCenter() != null) {
            claimsBuilder.claim("serviceCenterId", user.getServiceCenter().getId());
        } else {
            claimsBuilder.claim("serviceCenterId", null);
        }

        JWTClaimsSet jwtClaimsSet = claimsBuilder.build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            jwsObject.sign(new MACSigner(SIGN_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token: ", e);
            throw new RuntimeException(e);
        }
    }

    public void handleLogout(LogoutRequest request) throws JOSEException, ParseException {
        var verifyToken = verifyToken(request.getToken());
        String jwt = verifyToken.getJWTClaimsSet().getJWTID();
        Date expiryDate = verifyToken.getJWTClaimsSet().getExpirationTime();
        String subject = verifyToken.getJWTClaimsSet().getSubject();

        User user = userRepository.findByEmailOrPhoneNumber(subject, subject)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        InvalidToken invalidToken = InvalidToken.builder()
                .id(jwt)
                .expiryDate(expiryDate)
                .logoutAt(LocalDateTime.now())
                .user(user)
                .build();
        invalidTokenRepository.save(invalidToken);
    }

    @Override
    public UserResponse createUser(UserCreateRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is existed");
        }
        user.setPhoneNumber(request.getPhoneNumber());
        if (userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Phone number is existed");
        }
        user.setName(request.getName());
        user.setRole(User.Role.valueOf(request.getRole().toUpperCase()));
        user.setStatus(User.Status.ACTIVE);

        ServiceCenter sc = null;
        if (request.getServiceCenterId() != null) {
            sc = serviceCenterRepository.findById(request.getServiceCenterId())
                    .orElseThrow(() -> new NoSuchElementException("ServiceCenter not found"));
        }
        user.setServiceCenter(sc);

        String temporaryPassword = passwordGeneration.generateSimplePassword();
        user.setRequiresPasswordChange(true);
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        user.setRequiresPasswordChange(true);
        User saved = userRepository.save(user);

        sendAccountCreationEmail(
                saved.getName(),
                saved.getEmail(),
                temporaryPassword,
                saved.getRole(),
                loginUrl);

        return new UserResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getName(),
                saved.getPhoneNumber(),
                saved.getRole(),
                saved.getStatus(),
                getServiceCenterIdSafe(saved));
    }

    public UserResponse deleteUser(Long id, Long ownId) {
        if (id.equals(ownId)) {
            throw new IllegalArgumentException("You cannot deactivate your own account.");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        user.setStatus(User.Status.INACTIVE);
        User saved = userRepository.save(user);
        return new UserResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getName(),
                saved.getPhoneNumber(),
                saved.getRole(),
                saved.getStatus(),
                getServiceCenterIdSafe(saved));
    }

    @Override
    public UserResponse restoreUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        if (user.getStatus().equals(User.Status.INACTIVE)) {
            user.setStatus(User.Status.ACTIVE);
        } else {
            throw new IllegalArgumentException("User is already active");
        }
        User saved = userRepository.save(user);
        return new UserResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getName(),
                saved.getPhoneNumber(),
                saved.getRole(),
                saved.getStatus(),
                getServiceCenterIdSafe(saved));
    }

    @Override
    public UserResponse updateUser(UserUpdateRequest request, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        user.setId(id);

        if (userRepository.findByEmail(request.getEmail()).isPresent() && !user.getEmail().equals(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use by another user");
        }
        user.setEmail(request.getEmail());
        user.setName(request.getName());

        if (request.getRole() != null && !request.getRole().isEmpty()) {
            user.setRole(User.Role.valueOf(request.getRole().toUpperCase()));
        }
        if (request.getStatus() != null) {
            user.setStatus(User.Status.valueOf(request.getStatus().name().toUpperCase()));
        }

        ServiceCenter sc = null;
        if (request.getServiceCenterId() != null) {
            sc = serviceCenterRepository.findById(request.getServiceCenterId())
                    .orElseThrow(() -> new NoSuchElementException("ServiceCenter not found"));
        }
        user.setServiceCenter(sc);

        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()
                && !user.getPhoneNumber().equals(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number is already in use by another user");
        }
        user.setPhoneNumber(request.getPhoneNumber());
        User saved = userRepository.save(user);
        return new UserResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getName(),
                saved.getPhoneNumber(),
                saved.getRole(),
                saved.getStatus(),
                getServiceCenterIdSafe(saved));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> userRes = new ArrayList<>();
        for (User user : users) {
            userRes.add(new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getPhoneNumber(),
                    user.getRole(),
                    user.getStatus(),
                    getServiceCenterIdSafe(user)));
        }
        return userRes;
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + id));

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setName(user.getName());
        userResponse.setPhoneNumber(user.getPhoneNumber());
        userResponse.setRole(user.getRole());
        userResponse.setStatus(user.getStatus());
        userResponse.setServiceCenterId(getServiceCenterIdSafe(user));
        return userResponse;
    }

    @Override
    public List<UserResponse> searchUsers(UserSearchRequest request) {
        List<User> users = userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(
                        request.getKeyword(), request.getKeyword(), request.getKeyword());

        List<UserResponse> userRes = new ArrayList<>();
        for (User user : users) {
            userRes.add(new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getPhoneNumber(),
                    user.getRole(),
                    user.getStatus(),
                    getServiceCenterIdSafe(user)));
        }
        return userRes;
    }

    void sendAccountCreationEmail(
            String name,
            String recipientEmail,
            String password,
            User.Role role,
            String loginUrl) {

        String subject = "Account Access Notification";

        String htmlBody = String.format(
                "<html>" +
                        "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>" +
                        "<p>Dear %s,</p>" +
                        "<p>We are pleased to inform you that your account has been successfully created. Please find your login details below:</p>"
                        +
                        "<hr/>" +
                        "<p><strong>User (Email):</strong> %s</p>" +
                        "<p><strong>Password:</strong> %s</p>" +
                        "<p><strong>Role:</strong> %s</p>" +
                        "<hr/>" +
                        "<p>Please log in at: <a href='%s'>%s</a></p>" +
                        "<p>For your security, please <strong>change your password immediately</strong> after your first login.</p>"
                        +
                        "<p>Best regards,<br>" +
                        "OEM EV Warranty</p>" +
                        "</body>" +
                        "</html>",
                name,
                recipientEmail,
                password,
                role.toString(),
                loginUrl, loginUrl);

        EmailDetailsRequest details = new EmailDetailsRequest();
        details.setRecipient(recipientEmail);
        details.setSubject(subject);
        details.setMessageBody(htmlBody);

        emailService.sendHtmlMail(details);
    }

    @Override
    public UserResponse changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setRequiresPasswordChange(false);
        userRepository.save(user);

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getStatus(),
                getServiceCenterIdSafe(user));
    }

    @Override
    public String forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NoSuchElementException("Email not found"));
        if (!user.getStatus().equals(User.Status.ACTIVE)) {
            throw new IllegalArgumentException("User is not active");
        }
        if (user.isRequiresPasswordChange()) {
            throw new IllegalArgumentException("Password has not been changed");
        }
        String newPassword = passwordGeneration.generateSimplePassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setRequiresPasswordChange(true);
        userRepository.save(user);

        EmailDetailsRequest details = new EmailDetailsRequest();
        details.setRecipient(user.getEmail());
        details.setSubject("Password Reset - OEM EV Warranty");
        details.setMessageBody(
                "<p>Dear " + user.getName() + ",</p>" +
                        "<p>Your password has been reset. Here is your new password:</p>" +
                        "<p><b>" + newPassword + "</b></p>" +
                        "<p>Please log in and change your password immediately.</p>" +
                        "<p>Best regards,<br>OEM EV Warranty</p>");

        emailService.sendHtmlMail(details);
        return "Password reset email sent successfully.";
    }

    SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier jwsVerifier = new MACVerifier(SIGN_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expityDate = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verrfied = signedJWT.verify(jwsVerifier);
        if (!(verrfied && expityDate.after(new Date()))) {
            throw new RuntimeException("Verification failed or expired date passed");
        }
        if (invalidTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new RuntimeException("Verification failed or expired date passed");
        }
        return signedJWT;
    }

    public TechnicianPerformanceResponse calculateTechnicianPerformanceWithComparison(Long serviceCenterId) {
        boolean hasSpecificCenter = serviceCenterId != null && serviceCenterId > 0;

        LocalDate startOfThisMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfThisMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        LocalDate startOfLastMonth = startOfThisMonth.minusMonths(1);
        LocalDate endOfLastMonth = startOfThisMonth.minusDays(1);

        // Tháng này
        long totalThisMonth = hasSpecificCenter
                ? repairOrderRepository.countByServiceCenterIdAndStartDateBetween(
                        serviceCenterId, startOfThisMonth.atStartOfDay(), endOfThisMonth.plusDays(1).atStartOfDay())
                : repairOrderRepository.countAllOrdersBetween(startOfThisMonth.atStartOfDay(),
                        endOfThisMonth.plusDays(1).atStartOfDay());

        long completedThisMonth = hasSpecificCenter
                ? repairOrderRepository.countByServiceCenterIdAndStatusAndStartDateBetween(
                        serviceCenterId, RepairOrder.OrderStatus.COMPLETED,
                        startOfThisMonth.atStartOfDay(), endOfThisMonth.plusDays(1).atStartOfDay())
                : repairOrderRepository.countAllOrdersByStatusBetween(
                        RepairOrder.OrderStatus.COMPLETED, startOfThisMonth.atStartOfDay(),
                        endOfThisMonth.plusDays(1).atStartOfDay());

        double rateThisMonth = totalThisMonth == 0 ? 0 : (completedThisMonth * 100.0) / totalThisMonth;

        // Tháng trước
        long totalLastMonth = hasSpecificCenter
                ? repairOrderRepository.countByServiceCenterIdAndStartDateBetween(
                        serviceCenterId, startOfLastMonth.atStartOfDay(), endOfLastMonth.plusDays(1).atStartOfDay())
                : repairOrderRepository.countAllOrdersBetween(startOfLastMonth.atStartOfDay(),
                        endOfLastMonth.plusDays(1).atStartOfDay());

        long completedLastMonth = hasSpecificCenter
                ? repairOrderRepository.countByServiceCenterIdAndStatusAndStartDateBetween(
                        serviceCenterId, RepairOrder.OrderStatus.COMPLETED,
                        startOfLastMonth.atStartOfDay(), endOfLastMonth.plusDays(1).atStartOfDay())
                : repairOrderRepository.countAllOrdersByStatusBetween(
                        RepairOrder.OrderStatus.COMPLETED, startOfLastMonth.atStartOfDay(),
                        endOfLastMonth.plusDays(1).atStartOfDay());

        double rateLastMonth = totalLastMonth == 0 ? 0 : (completedLastMonth * 100.0) / totalLastMonth;

        double changePercent = rateLastMonth == 0
                ? (rateThisMonth > 0 ? 100 : 0)
                : ((rateThisMonth - rateLastMonth) / rateLastMonth) * 100;

        return TechnicianPerformanceResponse.builder()
                .currentRate(rateThisMonth)
                .changePercent(changePercent)
                .build();
    }

    public List<UserResponse> getTechniciansByServiceCenter(Long scId) {
        List<User> users = userRepository.findByRoleAndServiceCenterId(User.Role.TECHNICIAN, scId);
        return users.stream().map(UserResponse::fromEntity).toList();
    }

}