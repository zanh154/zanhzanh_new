package com.fptu.swp391.se1839.oemevwarrantymanagement.Utilities;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class PasswordGeneration {
    public String generateSimplePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(8);

        for (int i = 0; i < 4; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        for (int i = 0; i < 4; i++) {
            sb.append(numbers.charAt(random.nextInt(numbers.length())));
        }
        String password = sb.toString();
        char[] array = password.toCharArray();
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
        return new String(array);
    }
}