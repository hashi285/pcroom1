package org.example.pcroom.feature.pcroom.repository;

import org.example.pcroom.feature.pcroom.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByPcroomId(Long pcroomId);
}

