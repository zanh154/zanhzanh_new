package com.fptu.swp391.se1839.oemevwarrantymanagement.service;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.EmailDetailsRequest;

public interface EmailService {
    String sendHtmlMail(EmailDetailsRequest details);
}
