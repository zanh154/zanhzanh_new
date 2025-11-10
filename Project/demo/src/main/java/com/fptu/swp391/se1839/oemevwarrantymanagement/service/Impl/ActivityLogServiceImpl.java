package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.util.List;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Service;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.ActivityLog;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.ActivityLogRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.ActivityLogService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Aspect
@Service
@Transactional
@RequiredArgsConstructor
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public List<ActivityLog> findRecentActivities() {
        return activityLogRepository.findTop5ByOrderByCreatedAtDesc();
    }
}