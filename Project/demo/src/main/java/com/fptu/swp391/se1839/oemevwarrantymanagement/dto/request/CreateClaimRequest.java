package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request;

import java.util.List;

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
public class CreateClaimRequest {
    String description;
    String diagnosis;
    int mileage;
    String vin;
    String status;
    String priority;
    boolean agreeRecall;
    List<Long> defectivePartIds;
}
