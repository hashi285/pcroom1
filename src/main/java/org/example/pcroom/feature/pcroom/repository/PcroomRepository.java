package org.example.pcroom.feature.pcroom.repository;

import org.example.pcroom.feature.pcroom.entity.Pcroom;
import org.example.pcroom.feature.pcroom.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PcroomRepository extends JpaRepository<Pcroom, Long> {
//    // @Query: JPA에서 직접 JPQL(객체 기반 쿼리)을 작성할 때 사용한다.
//// 이 쿼리는 Pcroom 엔티티를 조회하면서 연관된 seats(좌석들)도 한 번에 함께 불러온다.
//    @Query("select u from Pcroom u join fetch u.seats where u.pcroomId = :pcroomId")

// pcroomId를 기준으로 특정 PC방을 찾아오며,
// 그 PC방에 연결된 모든 좌석(seats)을 미리(fetch) 한 번에 불러오는 메서드
//    Optional<Pcroom> findByPcroomIdWithSeat(@Param("pcroomId") Long pcroomId);

//// → 즉, 이 메서드는 pcroomId로 특정 PC방을 찾아올 뿐만 아니라,
////    PC방에 있는 좌석 목록(seats)도 함께 로딩해서 N+1 문제를 방지한다.
//@Query("select s from SetSeat s where s.pcroom.pcroomId = :pcroomId")
//    List<SetSeat> findSeatsByPcroomId(@Param("pcroomId") Long pcroomId);

//    @Query("SELECT p.nameOfPcroom FROM Pcroom p WHERE p.pcroomId = :pcroomId") // 예시로 name 필드
//    String findPcroomNameById(@Param("pcroomId") Long pcroomId);


Pcroom findByPcroomId(Long pcroomId);

    Optional<Pcroom> findByNameOfPcroom(String nameOfPcroom);
}


