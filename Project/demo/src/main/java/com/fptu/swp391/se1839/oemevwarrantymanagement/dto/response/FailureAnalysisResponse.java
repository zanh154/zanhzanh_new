package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FailureAnalysisResponse {

    List<ModelFailureResponse> failureRateByVehicleModel;

    List<claimsByCategoryResponse> claimsByCategory;

    List<ClaimsByComponentResponse> failuresByComponent;

    List<ClaimsByPriorityResponse> claimsByPriority;
}