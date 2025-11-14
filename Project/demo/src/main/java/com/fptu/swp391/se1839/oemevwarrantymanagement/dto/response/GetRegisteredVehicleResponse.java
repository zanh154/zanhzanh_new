package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import java.time.LocalDate;

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
public class GetRegisteredVehicleResponse {
    GetVehicleResponse vehicle;
    String code;
    String name;
    String description;
    LocalDate startDate;
    LocalDate endDate;
    CampaignVehicleStatus status;

    public enum CampaignVehicleStatus {
        ACTIVE, NOTIFIED, COMPLETED
    }
}