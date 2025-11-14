package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import java.util.Optional;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Part;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Vehicle;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.VehiclePart;

@Repository
public interface VehiclePartRepository extends JpaRepository<VehiclePart, String> {

  /**
   * Tìm bộ phận (part) đang gắn trên xe (vehicle) — chưa bị tháo (removalDate
   * IS NULL).
   */
  @Query("""
      SELECT vp
      FROM VehiclePart vp
      WHERE vp.vehicle = :vehicle
        AND vp.part = :part
        AND vp.removeDate IS NULL
      """)
  Optional<VehiclePart> findActiveVehiclePart(@Param("vehicle") Vehicle vehicle, @Param("part") Part part);

  /**
   * 🔹 Tìm VehiclePart theo VIN, PartId và ClaimId.
   */
  @Query("SELECT vp FROM VehiclePart vp WHERE vp.vehicle.vin = :vin AND vp.part.id = :partId AND vp.warrantyClaim.id = :claimId AND vp.removeDate IS NULL")
  Optional<VehiclePart> findActiveRemovedPart(
      @Param("vin") String vin,
      @Param("partId") Long partId,
      @Param("claimId") Long claimId);

  /**
   * 🔹 Tìm VehiclePart theo VIN và PartId (bất kể có claim hay chưa).
   */
  @Query("SELECT vp FROM VehiclePart vp WHERE vp.vehicle.vin = :vin AND vp.part.id = :partId AND vp.removeDate IS NULL")
  List<VehiclePart> findActiveByVehicleVinAndPartId(@Param("vin") String vin, @Param("partId") Long partId);

  @Query("SELECT vp FROM VehiclePart vp WHERE vp.oldSerialNumber= :serial")
  Optional<VehiclePart> findBySerial(@Param("serial") String serialNumber);

  @Query("SELECT vp FROM VehiclePart vp WHERE vp.vehicle.vin = :vin AND vp.part.id IN :partIds AND vp.removeDate IS NULL")
  List<VehiclePart> findActiveByVehicleVinAndPartIds(@Param("vin") String vin, @Param("partIds") List<Long> partIds);

}
