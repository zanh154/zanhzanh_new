package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetPartClaimResponse {
    long partClaimId;
    String partClaimName;
    String category;
    long quantity;
    String status;
    double estimatedCost;
    String policyName;
    String description;
    long durationPeriod;
    LocalDate effect;
    String coverage;
    String conditional;
}
