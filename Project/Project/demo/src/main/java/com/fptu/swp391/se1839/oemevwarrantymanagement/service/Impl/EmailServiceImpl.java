package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.EmailDetailsRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.EmailService;

import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailServiceImpl implements EmailService {

    @Autowired
    JavaMailSender javaMailSender;

    // Lấy email người gửi từ application.properties
    @Value("${spring.mail.username}")
    String sender;

    @Override
    public String sendHtmlMail(EmailDetailsRequest details) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");

            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setSubject(details.getSubject());
            mimeMessageHelper.setText(details.getMessageBody(), true);

            // Thêm BCC
            if (details.getBccList() != null && !details.getBccList().isEmpty()) {
                mimeMessageHelper.setBcc(details.getBccList().toArray(new String[0]));
            }

            javaMailSender.send(mimeMessage);
            return "HTML Email sent successfully";
        } catch (Exception e) {
            System.err.println("Error while sending HTML email: " + e.getMessage());
            return "Error while sending HTML email: " + e.getMessage();
        }
    }
}