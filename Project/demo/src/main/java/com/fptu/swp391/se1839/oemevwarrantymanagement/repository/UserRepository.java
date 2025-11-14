package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmailOrPhoneNumber(String email, String phoneNumber);

  Optional<User> findByPhoneNumber(String phoneNumber);

  Optional<User> findByEmail(String email);

  boolean existsById(Long id);

  List<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(
      String name, String email, String phoneNumber);

  List<User> findByRole(User.Role role);

  @Query("""
      SELECT u FROM User u
      WHERE (:serviceCenterId IS NULL OR u.serviceCenter.id = :serviceCenterId)
        AND u.workStatus IN :statuses
        AND u.role = :role
      """)
  List<User> findByWorkStatusInAndServiceCenterIdAndRole(
      @Param("statuses") List<User.WorkStatus> statuses,
      @Param("serviceCenterId") Long serviceCenterId,
      @Param("role") User.Role role);

  @Query("""
      SELECT u FROM User u
      WHERE u.workStatus IN :statuses
        AND u.role = :role
      """)
  List<User> findByWorkStatusInAndRole(
      @Param("statuses") List<User.WorkStatus> statuses,
      @Param("role") User.Role role);

  User findByName(String techName);

  @Query("""
      SELECT u FROM User u
      WHERE u.workStatus = :workStatus
        AND u.serviceCenter.id = :serviceCenterId
        AND u.role = :technician
      """)
  List<User> findByWorkStatusAndServiceCenterIdAndRole(
      @Param("workStatus") User.WorkStatus workStatus,
      @Param("serviceCenterId") Long serviceCenterId,
      @Param("technician") User.Role role);

  @Query("""
      SELECT u FROM User u
      WHERE u.workStatus = :workStatus
        AND u.role = :technician
      """)
  List<User> findByWorkStatusAndRole(
      @Param("workStatus") User.WorkStatus workStatus,
      @Param("technician") User.Role role);

  List<User> findByRoleAndServiceCenterId(User.Role role, Long serviceCenterId);

}