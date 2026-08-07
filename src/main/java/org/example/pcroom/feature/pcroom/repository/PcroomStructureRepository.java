package org.example.pcroom.feature.pcroom.repository;

import org.example.pcroom.feature.pcroom.entity.PcroomStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PcroomStructureRepository extends JpaRepository<PcroomStructure, Long> {
    List<PcroomStructure> findAllByPcroomId(Long pcroomId);
    void deleteAllByPcroomId(Long pcroomId);
}
