package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AllEvidenceRequest {
    String signature;
    String otpCode;
    String notes;
    List<MultipartFile> files;
}
