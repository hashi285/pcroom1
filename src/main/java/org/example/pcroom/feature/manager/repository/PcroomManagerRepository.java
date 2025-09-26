package org.example.pcroom.feature.manager.repository;

import org.example.pcroom.feature.manager.entity.PcroomManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PcroomManagerRepository extends JpaRepository<PcroomManager, Long> {
    void deletePcroomManagerByUserIdAndPcroomId(Long userId, Long pcroomId);
}
