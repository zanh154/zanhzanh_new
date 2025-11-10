package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import java.time.LocalDate;
import java.util.Set;

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
public class FilterClaimResponse {
    long id;
    int milege;
    int productYear;
    String modelName;
    long modelId;
    String vin;
    String licensePlate;
    String userName;
    String userPhoneNumber;
    String serviceCenterName;
    String description;
    String diagnosis;
    LocalDate claimDate;
    String senderName;
    double price;
    String priority;
    String currentStatus;
    Set<String> availableStatuses;
    String rejectReason;
    String statusRecall;
    String warrantyPolicyStatus;
    LocalDate purchaseDate;
}
