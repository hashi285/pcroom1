package org.example.pcroom.feature.pcroom.repository;

import org.example.pcroom.feature.pcroom.dto.SeatsDto;
import org.example.pcroom.feature.pcroom.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Query("SELECT new org. example. pcroom. feature. pcroom. dto. SeatsDto(s.zoneNumber) " +
            "FROM Seat s WHERE s.pcroom.pcroomId = :pcroomId")
    List<SeatsDto> findSeatsByPcroomId(@Param("pcroomId") Long pcroomId);
}

