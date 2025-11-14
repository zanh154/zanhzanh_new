package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AllEvidenceResponse {
    Long id;
    String evidenceType; // PHOTO, VIDEO, SIGNATURE, NOTE
    String url; // file path / URL (nếu có)
    String signature; // chữ ký (nếu có)
    String otpCode; // OTP (nếu có)
    String notes; // notes (nếu có)
    Long createdById;
    String createdByName; // optional
    String createdAt;
}
