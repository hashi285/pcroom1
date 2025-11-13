package org.example.pcroom.feature.pcroom.repository;

import org.example.pcroom.feature.pcroom.entity.Pcroom;
import org.example.pcroom.feature.pcroom.entity.Utilization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UtilizationRepository extends JpaRepository<Utilization, Long> {
    Optional<Utilization> findTopByPcroomIdOrderByTimeDesc(Long pcroomId);
}
