package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import java.util.List;
import java.util.Map;

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
public class DashboardResponse {
    Map<String, Object> dashboardMap;
    List<RecentActivityResponse> recentActiveList;
    Map<String, Integer> performanceMetrics;
    Map<String, Integer> urgentItems;
    Map<String, Object> quickStatistics;
}
