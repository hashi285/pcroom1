package org.example.pcroom.feature.pcroom.repository;

import jakarta.transaction.Transactional;
import org.example.pcroom.feature.pcroom.dto.SeatUsageDailyDTO;
import org.example.pcroom.feature.pcroom.entity.SeatUsageHourly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatUsageHourlyRepository extends JpaRepository<SeatUsageHourly, Long> {

    // 특정 좌석, 피시방, 날짜, 시간대별 데이터 조회
    @Query("""
        SELECT s 
        FROM SeatUsageHourly s 
        WHERE s.seatId = :seatId 
          AND s.pcroomId = :pcroomId
          AND FUNCTION('DATE', s.createdAt) = :date
          AND FUNCTION('HOUR', s.createdAt) = :hour
    """)
    Optional<SeatUsageHourly> findBySeatIdAndPcroomIdAndDateHour(
            @Param("seatId") Long seatId,
            @Param("pcroomId") Long pcroomId,
            @Param("date") LocalDate date,
            @Param("hour") int hour
    );

    // 하루 단위로 사용량 합산
    @Query("""
        SELECT new org.example.pcroom.feature.pcroom.dto.SeatUsageDailyDTO(
            s.seatId,
            s.pcroomId,
            SUM(s.usedSeconds)
        )
        FROM SeatUsageHourly s
        WHERE FUNCTION('DATE', s.createdAt) = :date
        GROUP BY s.seatId, s.pcroomId
    """)
    List<SeatUsageDailyDTO> aggregateDaily(@Param("date") LocalDate date);

    // 특정 날짜의 데이터 삭제
    @Modifying
    @Transactional
    @Query("""
        DELETE FROM SeatUsageHourly s
        WHERE FUNCTION('DATE', s.createdAt) = :date
    """)
    void deleteByDate(@Param("date") LocalDate date);


    List<SeatUsageHourly> findByPcroomIdAndCreatedAt(Long pcroomId, LocalDateTime createdAt);


    // 특정 PC방, 특정 시간 범위에 해당하는 Hourly 조회
    List<SeatUsageHourly> findByPcroomIdAndCreatedAtBetween(Long pcroomId, LocalDateTime start, LocalDateTime end);


}
