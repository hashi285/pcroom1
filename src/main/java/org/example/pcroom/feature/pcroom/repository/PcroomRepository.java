package org.example.pcroom.feature.pcroom.repository;

import org.example.pcroom.feature.pcroom.entity.Pcroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PcroomRepository extends JpaRepository<Pcroom, Long> {

    Optional<Pcroom> findByPcroomId(Long pcroomId);

    Optional<Pcroom> findByNameOfPcroom(String nameOfPcroom);

    // 이름에 특정 문자열이 포함된 피시방 검색 (LIKE %name%)
    List<Pcroom> findByNameOfPcroomContaining(String name);

    // 또는 LIKE 수동 지정
    @Query("SELECT p FROM Pcroom p WHERE p.nameOfPcroom LIKE %:name%")
    List<Pcroom> searchByNameLike(@Param("name") String name);

    List<Pcroom> findByNameOfPcroomIn(List<String> nameOfPcroom);

    List<Pcroom> findAllByOrderByNameOfPcroom();

    @Query("SELECT p.pcroomId FROM Pcroom p")
    List<Long> findAllPcroomIds();

    @Query("SELECT p.nameOfPcroom FROM Pcroom p WHERE p.pcroomId = :pcroomId")
    String findNameByPcroomId(@Param("pcroomId") Long pcroomId);

    List<Pcroom> findAllByPcroomIdIn(List<Long> pcroomIds);



}


