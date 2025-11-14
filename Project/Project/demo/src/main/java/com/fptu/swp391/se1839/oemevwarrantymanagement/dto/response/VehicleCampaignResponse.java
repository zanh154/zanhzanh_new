package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleCampaignResponse {
    String vin;
    String model;
    String customerName;
    String customerEmail;
    String customerPhone;
}