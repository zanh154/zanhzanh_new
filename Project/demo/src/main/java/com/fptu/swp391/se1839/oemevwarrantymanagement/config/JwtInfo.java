package com.fptu.swp391.se1839.oemevwarrantymanagement.config;

public class JwtInfo {
    private Long userId;
    private String role;
    private Long serviceCenterId;

    public JwtInfo(Long userId, String role, Long serviceCenterId) {
        this.userId = userId;
        this.role = role;
        this.serviceCenterId = serviceCenterId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public Long getServiceCenterId() {
        return serviceCenterId;
    }
}
