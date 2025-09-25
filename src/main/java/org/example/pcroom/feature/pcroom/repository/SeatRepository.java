package org.example.pcroom.feature.pcroom.repository;

import org.example.pcroom.feature.pcroom.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByPcroomId(Long pcroomId);
    @Query("SELECT s FROM Seat s JOIN IpResult i ON s.id = i.seatId " +
            "WHERE s.pcroomId = :pcroomId AND i.result = true")
    List<Seat> findAvailableSeatsByPcroomId(@Param("pcroomId") Long pcroomId);
}

