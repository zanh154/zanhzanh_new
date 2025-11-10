package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepairDetailResponse {
    long id;
    String partName;
    String oldSerialNumber;
    long quantity;
    int productYear;
    String modelName;
    String vin;
    String licensePlate;
    String category;

    LocalDate installationDate;
    String technicianName;
    String replacementDescription;
    String newSerialNumber;

}