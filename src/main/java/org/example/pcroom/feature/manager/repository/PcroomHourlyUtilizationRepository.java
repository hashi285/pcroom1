package org.example.pcroom.feature.manager.repository;

import org.example.pcroom.feature.manager.entity.PcroomHourlyUtilization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PcroomHourlyUtilizationRepository extends JpaRepository<PcroomHourlyUtilization, Long> {

    // 특정 피시방(pcroomId)과 기록 시각(recordedAt)이 이미 존재하는지 여부 확인
    boolean existsByPcroomIdAndTime(Long pcroomId, java.time.LocalDateTime recordedAt);

    List<PcroomHourlyUtilization> findByPcroomIdInAndTimeAfter(List<Long> pcroomIds, LocalDateTime fromTime);


}
