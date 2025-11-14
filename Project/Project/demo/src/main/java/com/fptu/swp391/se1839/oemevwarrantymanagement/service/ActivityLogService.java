package com.fptu.swp391.se1839.oemevwarrantymanagement.service;

import java.util.List;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.ActivityLog;

public interface ActivityLogService {
    List<ActivityLog> findRecentActivities();
}
