package org.example.pcroom.feature.manager.repository;

import org.example.pcroom.feature.manager.entity.CompetitorRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetitorRelationRepository extends JpaRepository<CompetitorRelation, Long> {
    void deletePcroomManagerByUserIdAndPcroomId(Long userId, Long pcroomId);
    @Query("SELECT p.pcroomId FROM CompetitorRelation p WHERE p.userId = :userId")
    List<Long> findPcroomIdByUserId(@Param("userId") Long userId);}
