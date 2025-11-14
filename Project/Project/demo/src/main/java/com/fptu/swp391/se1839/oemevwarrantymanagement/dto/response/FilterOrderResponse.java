package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import java.time.LocalDateTime;

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
public class FilterOrderResponse {
    long repairOrderId;
    long claimId;
    String claimStatus;
    int prodcutYear;
    String modelName;
    String vin;
    String licensePlate;
    String techinal;
    double percentInProcess;
    LocalDateTime orderDate;
    boolean status;
}
