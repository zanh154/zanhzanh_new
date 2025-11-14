package com.fptu.swp391.se1839.oemevwarrantymanagement.service;

import java.util.List;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.RecallCampaignRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.RecallVehiclesRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.RecallCampaignResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.RecallProgressResponse;

public interface RecallCampaignService {
    // Tạo chiến dịch recall mới
    RecallCampaignResponse createCampaign(RecallCampaignRequest request);

    // Cập nhật chiến dịch
    RecallCampaignResponse updateCampaign(Long campaignId, RecallCampaignRequest request);

    // Thêm xe vào chiến dịch
    List<RecallProgressResponse> addVehiclesToCampaign(Long campaignId, RecallVehiclesRequest request);

    // Cập nhật trạng thái recall cho một xe
    RecallProgressResponse updateVehicleStatus(Long campaignId, String vin, String status, String notes);

    // Lấy danh sách chiến dịch
    List<RecallCampaignResponse> getAllCampaigns();

    // Lấy chi tiết chiến dịch
    RecallCampaignResponse getCampaignById(Long campaignId);

    // Lấy danh sách xe trong chiến dịch
    List<RecallProgressResponse> getVehiclesInCampaign(Long campaignId);

    // Lấy danh sách xe theo trạng thái
    List<RecallProgressResponse> getVehiclesByStatus(Long campaignId, String status);

    // Xóa chiến dịch
    void deleteCampaign(Long campaignId);
}