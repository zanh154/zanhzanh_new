package com.fptu.swp391.se1839.oemevwarrantymanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {
		org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
		org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class
})
@EnableScheduling
public class WarrantyManagement {

	public static void main(String[] args) {
		SpringApplication.run(WarrantyManagement.class, args);
	}

}
