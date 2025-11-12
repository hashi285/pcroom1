package org.example.pcroom.feature.notice.repository;

import org.example.pcroom.feature.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice,Long> {
    List<Notice> findNoticesByPcroomId(Long pcroomId);
    Optional<Notice> findNoticeById(Long id);
}
