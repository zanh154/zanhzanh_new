package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.CampaignVehicle;

@Repository
public interface CampaignVehicleRepository extends JpaRepository<CampaignVehicle, Long> {

    @Query("SELECT cv FROM CampaignVehicle cv WHERE cv.vehicle.vin = :vin")
    List<CampaignVehicle> findAllByVehicleVin(@Param("vin") String vin);

    @Query("SELECT cv FROM CampaignVehicle cv WHERE cv.vehicle.vin = :vin")
    Optional<CampaignVehicle> findByVehicleVin(@Param("vin") String vin);

    List<CampaignVehicle> findByServiceCampaignId(Long serviceCampaignId);

    Optional<CampaignVehicle> findByVehicleVinAndServiceCampaignId(String vin, Long serviceCampaignId);

    boolean existsByServiceCampaignIdAndVehicleVin(Long campaignId, String vin);

    @Query("""
            SELECT DISTINCT cv
                FROM CampaignVehicle cv
                JOIN cv.vehicle v
                JOIN v.customer cust
                JOIN cust.createdBy u
                WHERE u.serviceCenter.id = :scId
                """)
    List<CampaignVehicle> findByServiceCenterId(@Param("scId") Long scId);

}
