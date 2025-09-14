package org.example.pcroom.feature.pcroom.repository;

import org.example.pcroom.feature.pcroom.entity.IpResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IpResultRepository extends JpaRepository<IpResult, Long> {
    Optional<IpResult> findTopByPcroomId(Long pcroomId);
}
