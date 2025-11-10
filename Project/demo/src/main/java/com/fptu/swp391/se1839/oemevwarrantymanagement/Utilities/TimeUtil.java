package com.fptu.swp391.se1839.oemevwarrantymanagement.Utilities;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtil {
    public static String formatTimeAgo(LocalDateTime createdAt) {
        if (createdAt == null)
            return "";
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        long minutes = duration.toMinutes();

        if (minutes < 1)
            return "vừa xong";
        if (minutes < 60)
            return minutes + " phút trước";
        long hours = duration.toHours();
        if (hours < 24)
            return hours + " giờ trước";
        long days = duration.toDays();
        return days + " ngày trước";
    }
}
