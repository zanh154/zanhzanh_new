package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import java.time.LocalDate;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.CampaignVehicle.CampaignVehicleStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleInCampaignResponse {
    String campaignName;
    String vin;
    String customerName;
    String email;
    String phoneNumber;
    String address;
    LocalDate startDate;
    LocalDate endDate;
    CampaignVehicleStatus status;
    
}
