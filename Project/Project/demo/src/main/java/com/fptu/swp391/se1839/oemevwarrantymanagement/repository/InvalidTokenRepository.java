package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.InvalidToken;

@Repository
public interface InvalidTokenRepository extends JpaRepository<InvalidToken, String> {
    @Query("SELECT i FROM InvalidToken i WHERE i.user.email = :email OR i.user.phoneNumber = :phone ORDER BY i.logoutAt DESC LIMIT 1")
    Optional<InvalidToken> findLastByUser(@Param("email") String email, @Param("phone") String phone);

    @Query("SELECT i FROM InvalidToken i " +
            "WHERE i.user.email = :email " +
            "AND i.user.phoneNumber = :phoneNumber " +
            "AND FUNCTION('DATE', i.logoutAt) = :date")
    List<InvalidToken> findByUserEmailAndPhoneNumberAndLogoutDate(
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber,
            @Param("date") LocalDate date);
}
