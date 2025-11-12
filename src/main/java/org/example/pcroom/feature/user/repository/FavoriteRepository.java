package org.example.pcroom.feature.user.repository;

import org.example.pcroom.feature.user.dto.FavoriteDto;
import org.example.pcroom.feature.user.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // 특정 유저가 특정 피시방을 즐겨찾기 했는지 체크
    boolean existsByUserIdAndPcroomId(Long userId, Long pcroomId);

    // 특정 즐겨찾기 삭제
    void deleteByUserIdAndPcroomId(Long userId, Long pcroomId);


    // 특정 유저가 즐겨찾기한 PC방 id + 이름 조회
    @Query("SELECT new org.example.pcroom.feature.user.dto.FavoriteDto(p.pcroomId, p.nameOfPcroom) " +
            "FROM Pcroom p JOIN Favorite f ON f.pcroomId = p.pcroomId " +
            "WHERE f.userId = :userId")
    List<FavoriteDto> findFavoritePcroomsByUserId(@Param("userId") Long userId);

    List<Favorite> findByUserId(Long userId);



}
