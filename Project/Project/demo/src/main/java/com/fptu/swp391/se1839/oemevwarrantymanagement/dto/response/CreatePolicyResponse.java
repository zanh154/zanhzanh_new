package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.WarrantyPolicy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePolicyResponse {
    boolean success;
    String message;
    WarrantyPolicy policy;
}