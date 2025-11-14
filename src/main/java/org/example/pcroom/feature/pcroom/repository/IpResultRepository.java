package org.example.pcroom.feature.pcroom.repository;

import org.example.pcroom.feature.pcroom.entity.IpResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IpResultRepository extends JpaRepository<IpResult, Long> {

    @Query("""
    SELECT i
    FROM IpResult i
    WHERE i.pcroomId = :pcroomId
      AND i.createdTime <= :now
      AND i.createdTime = (SELECT MAX(i2.createdTime)
                           FROM IpResult i2
                           WHERE i2.pcroomId = :pcroomId
                             AND i2.seatId = i.seatId
                             AND i2.createdTime <= :now)
    """)
    List<IpResult> findLatestByPcroomIdBeforeNow(@Param("pcroomId") Long pcroomId,
                                                 @Param("now") LocalDateTime now);

}
