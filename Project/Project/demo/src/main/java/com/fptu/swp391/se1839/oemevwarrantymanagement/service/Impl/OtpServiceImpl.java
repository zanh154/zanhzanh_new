package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.User;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.UserOtp;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.UserOtpRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OtpServiceImpl {

    @Autowired
    UserOtpRepository otpRepository;

    @Autowired
    JavaMailSender mailSender;

    // Gửi OTP
    public void sendOtp(User user) {
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000); // 6 chữ số

        // Lưu OTP vào DB
        UserOtp userOtp = new UserOtp();
        userOtp.setEmail(user.getEmail());
        userOtp.setPhoneNumber(user.getPhoneNumber());
        userOtp.setOtp(otp);
        userOtp.setExpiry(LocalDateTime.now().plusMinutes(5));
        otpRepository.save(userOtp);

        // Gửi Email nếu có email
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            sendOtpEmail(user.getEmail(), otp);
        }

        // DEV log
        log.info("OTP for {} / {} is {}", user.getEmail(), user.getPhoneNumber(), otp);
    }

    void sendOtpEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP is: " + otp + "\nIt will expire in 5 minutes.");
        mailSender.send(message);
    }

    public boolean verifyOtp(String emailOrPhone, String otp) {
        UserOtp userOtp = otpRepository.findByOtp(otp).stream()
                .filter(u -> emailOrPhone.equals(u.getEmail()) || emailOrPhone.equals(u.getPhoneNumber()))
                .findFirst().orElse(null);

        if (userOtp != null && userOtp.getExpiry().isAfter(LocalDateTime.now())) {
            otpRepository.delete(userOtp); // xóa sau khi dùng
            return true;
        }
        return false;
    }
}
