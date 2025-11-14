package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

        Optional<Vehicle> findByVin(String vin);

        List<Vehicle> findByProductionDateBetween(LocalDate from, LocalDate to);

        @Query("""
                        SELECT v FROM Vehicle v
                        Where v.customer.phoneNumber = :phone
                        """)
        List<Vehicle> findByCustomerPhone(@Param("phone") String phone);

        Optional<Vehicle> findByLicensePlate(String licensePlate);

        boolean existsByLicensePlate(String licensePlate);

        List<Vehicle> findByCustomerId(Long customerId);

        @Query("SELECT v FROM Vehicle v WHERE v.customer IS NOT NULL")
        List<Vehicle> findAllRegisteredVehicles();

        @Query("""
                        SELECT DISTINCT v FROM Vehicle v
                        LEFT JOIN FETCH v.campaignVehicles
                        WHERE v.customer.id = :customerId
                        """)
        List<Vehicle> findAllByCustomerIdWithCampaigns(@Param("customerId") Long customerId);

}
