package com.fptu.swp391.se1839.oemevwarrantymanagement.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dd3cfpkj4",
                "api_key", "297934939846551",
                "api_secret", "N0oy03YuIGZmXs08Qf0Dsv8IeSo",
                "secure", true));
    }
}