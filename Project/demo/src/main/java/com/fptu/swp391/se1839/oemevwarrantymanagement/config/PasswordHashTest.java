package com.fptu.swp391.se1839.oemevwarrantymanagement.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "123456"; // password thật
        String hashedPassword = encoder.encode(rawPassword);
        System.out.println(hashedPassword); // copy nguyên chuỗi này vào DB
    }
}
