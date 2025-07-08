package org.example.pcroom.feature.pcroom.repository;

import org.example.pcroom.feature.pcroom.entity.Pcroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PcroomRepository extends JpaRepository<Pcroom, Long> {
}
