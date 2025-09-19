package org.example.pcroom.feature.pcroom.repository;

import org.example.pcroom.feature.pcroom.entity.Pcroom;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PcroomRepository extends JpaRepository<Pcroom, Long> {

    Optional<Pcroom> findByPcroomId(Long pcroomId);

    Optional<Pcroom> findByNameOfPcroom(String nameOfPcroom);


}


