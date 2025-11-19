package org.example.pcroom.feature.pcroom.repository;

import org.example.pcroom.feature.pcroom.entity.SeatUsageDaily;
import org.example.pcroom.feature.pcroom.entity.SeatUsageHourly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface SeatUsageDailyRepository extends JpaRepository<SeatUsageDaily, Long> {

    List<SeatUsageDaily> findAllByPcroomIdAndDateBetween(Long pcroomId, LocalDate start, LocalDate end);
    List<SeatUsageDaily> findByPcroomIdAndDateBetween(Long pcroomId, LocalDate start, LocalDate end);
}