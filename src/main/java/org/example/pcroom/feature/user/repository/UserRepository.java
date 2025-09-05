package org.example.pcroom.feature.user.repository;

import org.example.pcroom.feature.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserId(Long userId);
    List<User> findAll();
//    @Query("SELECT u FROM User u JOIN FETCH u.pcrooms WHERE u.userId = :userId")
//    Optional<User> findByIdWithPcrooms(@Param("userId") Long userId);

    // 설명 필요
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
