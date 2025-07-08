package org.example.pcroom.feature.pcroom.repository;

import org.example.pcroom.feature.pcroom.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
}
