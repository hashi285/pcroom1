package org.example.pcroom.feature.notice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.notice.dto.NoticeDto;
import org.example.pcroom.feature.notice.entity.Notice;
import org.example.pcroom.feature.notice.repository.NoticeRepository;
import org.example.pcroom.feature.user.repository.FavoriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final FavoriteRepository favoriteRepository;


    @Transactional // 공지사항 저장
    public Notice createNotice(Notice notice){
        Notice newNotice = new Notice();
        newNotice.setTitle(notice.getTitle());
        newNotice.setContent(notice.getContent());
        newNotice.setUserId(notice.getUserId());
        newNotice.setPcroomId(notice.getPcroomId());

        return noticeRepository.save(newNotice);
    }

    @Transactional // 공지사항 수정
    public Notice updateNotice(Long noticeId, Long userId){return null;}

    @Transactional // 공지사항 삭제
    public void deleteNotice(Long noticeId, Long userId) throws AccessDeniedException {
        Notice notice = noticeRepository.findNoticeById(noticeId)
                .orElseThrow(()-> new EntityNotFoundException("공지가 없습니다."));
       if(notice.getUserId().equals(userId)){
           noticeRepository.deleteById(noticeId);
       } else {
           throw new AccessDeniedException("작성자만 삭제할 수 있습니다.");
       }
    }

    @Transactional // 제목을 list형태로 반환한다. (해당 피시방의 사용자만 볼 수 있음)
    public List<NoticeDto.NoticeTitleDto> findNotice(Long pcroomId){

        List<Notice>  notices = noticeRepository.findNoticesByPcroomId(pcroomId);

        return notices.stream()
                .map(notice -> new NoticeDto.NoticeTitleDto(
                        notice.getId(),
                        notice.getTitle(),
                        notice.getCreationDate() // 엔티티에 LocalDateTime 필드가 있어야 함
                ))
                .toList();
    }

    @Transactional // 공지사항을 회원에게 보여준다.
    public NoticeDto.NoticeDetailDto findNoticeDetail(Long noticeId){
        Optional<Notice> notice = noticeRepository.findNoticeById(noticeId);

        return new NoticeDto.NoticeDetailDto(
                notice.get().getId(),
                notice.get().getTitle(),
                notice.get().getContent(),
                notice.get().getCreationDate()
        );
    }
}
