package org.example.pcroom.feature.pcroom.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.pcroom.dto.IpResultDto;
import org.example.pcroom.feature.pcroom.dto.PcroomDto;
import org.example.pcroom.feature.pcroom.dto.PingUtilizationDto;
import org.example.pcroom.feature.pcroom.dto.SeatsDto;
import org.example.pcroom.feature.pcroom.entity.IpResult;
import org.example.pcroom.feature.pcroom.entity.Pcroom;
import org.example.pcroom.feature.pcroom.entity.Seat;
import org.example.pcroom.feature.pcroom.repository.IpResultRepository;
import org.example.pcroom.feature.pcroom.repository.PcroomRepository;
import org.example.pcroom.feature.pcroom.repository.SeatRepository;
import org.example.pcroom.global.config.redis.PcroomSeatStatusCacheRepository;
import org.example.pcroom.global.config.redis.PcroomStatusCacheRepository;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PcroomService {

    private final PingService pingService;
    private final PcroomRepository pcroomRepository;
    private final SeatRepository seatRepository;
    private final IpResultRepository ipResultRepository;
    private final PcroomStatusCacheRepository pcroomStatusCacheRepository;
    private final PcroomSeatStatusCacheRepository pcroomSeatStatusCacheRepository;


    @Transactional(readOnly = true)
    public PingUtilizationDto getStatusFromCache(Long pcroomId) throws Exception {

        Pcroom pcroom = pcroomRepository.findByPcroomId(pcroomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid PC Room ID: " + pcroomId));

        Integer seatCount = pcroom.getSeatCount();

        PingUtilizationDto.UtilizationAndResults cached = pcroomStatusCacheRepository.getPcroomStatus(pcroomId);

        if (cached == null) {
            cached = pingService.ping(pcroomId);

        }

        int activeSeats = (int) cached.getResult().stream()
                .filter(IpResult::getResult)
                .count();

        double utilization = ((double) activeSeats / seatCount) * 100.0;

        return new PingUtilizationDto(
                pcroomId,
                pcroom.getNameOfPcroom(),
                utilization,
                seatCount,
                activeSeats
        );
    }


    @Transactional(readOnly = true)
    public List<IpResultDto.SeatStatusDto> getSeatStatusFromCache(Long pcroomId) throws Exception {

        List<IpResultDto.SeatStatusDto>  seatStatusDtoList = pcroomSeatStatusCacheRepository.getPcroomStatus(pcroomId);

        if (seatStatusDtoList == null) {
            pingService.ping(pcroomId);
        }

        return seatStatusDtoList;

    }


    /**
     * 신규 PC방 등록
     */
    @Transactional
    public PcroomDto.ReadPcRoomResponse registerNewPcroom(PcroomDto.CreatePcRoomRequest request) {

        var pcroom = Pcroom.register(
                request.getNameOfPcroom(),
                request.getSeatCount(),
                request.getPort(),
                request.getWidth(),
                request.getHeight()
        );

        pcroomRepository.save(pcroom);

        return new PcroomDto.ReadPcRoomResponse(
                pcroom.getPcroomId(),
                pcroom.getNameOfPcroom(),
                pcroom.getSeatCount(),
                pcroom.getPort(),
                pcroom.getWidth(),
                pcroom.getHeight()
        );
    }


    /**
     * 좌석 등록 (PC방 전체 좌석 일괄 등록 방식)
     *
     * 정책: 좌석 입력 개수 != 등록된 수량 → 예외 처리
     */
    @Transactional
    public List<SeatsDto> registerNewSeat(List<SeatsDto> seatsDtos) {

        String nameOfPcroom = seatsDtos.getFirst().getNameOfPcroom();

        Pcroom pcroom = pcroomRepository.findByNameOfPcroom(nameOfPcroom)
                .orElseThrow(() -> new IllegalArgumentException("해당 피시방 없음: " + nameOfPcroom));

        int seatNum = pcroom.getSeatCount();

        if (seatsDtos.size() != seatNum) {
            throw new IllegalArgumentException(nameOfPcroom + " 피시방은 좌석을 " + seatNum + "개만 입력할 수 있습니다.");
        }

        List<Seat> seats = seatsDtos.stream()
                .map(dto -> dto.toEntity(pcroom))
                .toList();

        seatRepository.saveAll(seats);

        return seatsDtos;
    }


    /**
     * PC방 검색 (목록 조회)
     */
    @Transactional
    public List<PcroomDto> searchPcrooms(String name) {
        return pcroomRepository.findByNameOfPcroomContaining(name).stream()
                .map(PcroomDto::fromEntity)
                .toList();
    }


    /**
     * PC방 환경 정보 조회 (레이아웃 등)
     */
    @Transactional
    public PcroomDto.PcroomInfo getPcroomInfo(Long pcroomId) {
        Pcroom pcroom = pcroomRepository.findById(pcroomId)
                .orElseThrow(() -> new EntityNotFoundException("PC방 정보 없음. ID=" + pcroomId));

        return new PcroomDto.PcroomInfo(
                pcroom.getNameOfPcroom(),
                pcroom.getWidth(),
                pcroom.getHeight()
        );
    }


    /**
     * 좌석 좌표 정보 조회
     * 클라이언트 UI 렌더링에 사용됨
     */
    @Transactional
    public List<PcroomDto.seatInfo> seatInfo(Long pcroomId) {
        return seatRepository.findByPcroomId(pcroomId).stream()
                .map(seat -> new PcroomDto.seatInfo(
                        pcroomId,
                        seat.getSeatsNum(),
                        seat.getX(),
                        seat.getY()
                ))
                .toList();
    }


    /**
     * 가장 최신 Ping 결과 조회
     * Redis 캐시가 아니라 DB Persistence Layer에서 조회하는 방식
     */
    @Transactional
    public List<IpResultDto.SeatStatusDto> getLatestSeatResults(Long pcroomId) {

        long start = System.currentTimeMillis();

        List<IpResult> latest = ipResultRepository.findLatestByPcroomIdBeforeNow(pcroomId, LocalDateTime.now());

        log.info("[getLatestSeatResults] DB Latency={}ms count={}",
                (System.currentTimeMillis() - start), latest.size());

        return latest.stream()
                .map(result -> {

                    Seat seat = seatRepository.findById(result.getSeatId())
                            .orElseThrow(() -> new EntityNotFoundException("좌석 정보 없음 seatId=" + result.getSeatId()));

                    return new IpResultDto.SeatStatusDto(seat.getSeatsNum(), result.getResult());
                })
                .toList();
    }
}
