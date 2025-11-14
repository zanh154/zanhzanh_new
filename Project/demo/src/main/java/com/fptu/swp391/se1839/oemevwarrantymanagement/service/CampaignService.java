package com.fptu.swp391.se1839.oemevwarrantymanagement.service;

import java.util.List;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.CreateCampaignRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.UpdateCampaignRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetAllCampaignResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetAllVehicleCampaignResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ServiceCampaignDetailResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ServiceCampaignResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ServiceCampaignSummaryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.VehicleInCampaignResponse;

public interface CampaignService {
    ServiceCampaignResponse handleCreateCampaign(CreateCampaignRequest request);

    GetAllCampaignResponse handleGetAllCampaigns();

    ServiceCampaignDetailResponse handleGetCampaignById(Long id);

    void handleDeleteCampaign(Long id);

    ServiceCampaignResponse handleUpdateCampaign(UpdateCampaignRequest request, Long id);

    List<ServiceCampaignSummaryResponse> handleGetCampaignByVin(String vin);


    GetAllVehicleCampaignResponse handleGetAllVehiclesWithCampaigns();

    String notifyCustomersByCampaign(Long campaignId);

	List<VehicleInCampaignResponse> handleGetVehiclesInCampaignByServiceCenter(Long scId);
}
