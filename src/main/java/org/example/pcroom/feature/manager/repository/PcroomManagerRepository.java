package org.example.pcroom.feature.manager.repository;

import org.example.pcroom.feature.manager.entity.PcroomManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PcroomManagerRepository extends JpaRepository<PcroomManager,Long> {
    List<PcroomManager> findAllByUserId(Long userId);
    void deleteByUserIdAndPcroomId(Long userId, Long pcroomId);


}
