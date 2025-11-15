package org.example.pcroom.feature.pcroom.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;


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


    /**
     * @param pcRoomId 조회할 피시방 ID
     * @return String 형식으로 피시방 가동률  반환
     * @throws Exception
     */
    @Transactional
    public PingUtilizationDto canUseSeat(Long pcRoomId) throws Exception {
        double utilization = pingService.ping(pcRoomId);
        Optional<Pcroom> pcroom = pcroomRepository.findByPcroomId(pcRoomId);
        String name = pcroom.get().getNameOfPcroom();
        Integer seatCount = pcroomRepository.findByPcroomId(pcRoomId)
                .map(Pcroom::getSeatCount)
                .orElse(0);

        Integer usedSeatCount = (int) (seatCount * utilization/100);



        return new PingUtilizationDto(
                pcRoomId,
                name,
                utilization,
                seatCount,
                usedSeatCount
        );
    }

    /**
     * 피시방 저장
     *
     * @param request
     * @return
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
     * 피시방 자리 저장
     * @param seatsDtos
     * @return
     */
    @Transactional
    public List<SeatsDto> registerNewSeat(List<SeatsDto> seatsDtos) {
        String nameOfPcroom = seatsDtos.getFirst().getNameOfPcroom();
        Pcroom pcroom = pcroomRepository.findByNameOfPcroom(nameOfPcroom)
                .orElseThrow(() -> new IllegalArgumentException("해당 피시방 없음: " + nameOfPcroom));
        int seatNum = pcroom.getSeatCount();
        if(seatsDtos.size() != seatNum) {
            throw new IllegalArgumentException(nameOfPcroom + "피시방은 좌석을 " + seatNum + "개만 입력이 가능합니다.");
        }

        List<Seat> seats = seatsDtos.stream()
                .map(dto -> dto.toEntity(pcroom))
                .toList();

        seatRepository.saveAll(seats);
        return null;
    }

    /**
     * 피시방 검색
     * @param name
     * @return
     */
    @Transactional
    public List<PcroomDto> searchPcrooms(String name) {
        return pcroomRepository.findByNameOfPcroomContaining(name).stream()
                .map(PcroomDto::fromEntity)
                .toList();
    }



    @Transactional
    public PcroomDto.PcroomInfo getPcroomInfo(Long pcroomId) {
        Pcroom pcroom = pcroomRepository.findById(pcroomId)
                .orElseThrow(() -> new EntityNotFoundException("해당 피시방을 찾을 수 없습니다. ID=" + pcroomId));

        return new PcroomDto.PcroomInfo(
                pcroom.getNameOfPcroom(),
                pcroom.getWidth(),
                pcroom.getHeight()
        );
    }

    @Transactional // 좌석별 정보 조회
    public List<PcroomDto.seatInfo> seatInfo(Long pcroomId) {
        return seatRepository.findByPcroomId(pcroomId).stream()
                .map(seat -> new PcroomDto.seatInfo(
                        pcroomId, // 또는 seat.getPcroomId()
                        seat.getSeatsNum(),
                        seat.getX(),
                        seat.getY()
                ))
                .toList();
    }

    @Transactional
    public List<IpResultDto.SeatStatusDto> getLatestSeatResults(Long pcroomId) {
        long startTotal = System.currentTimeMillis();

        long startLatestQuery = System.currentTimeMillis();
        List<IpResult> latestSeats = ipResultRepository
                .findLatestByPcroomIdBeforeNow(pcroomId, LocalDateTime.now());
        long endLatestQuery = System.currentTimeMillis();
        log.info("[getLatestSeatResults] Latest IpResult fetch: {} ms, count={}",
                (endLatestQuery - startLatestQuery), latestSeats.size());

        long startSeatLookup = System.currentTimeMillis();
        List<IpResultDto.SeatStatusDto> result = latestSeats.stream()
                .map(ipResult -> {
                    long seatStart = System.currentTimeMillis();
                    Seat seat = seatRepository.findById(ipResult.getSeatId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Seat not found. seatId=" + ipResult.getSeatId()));
                    long seatEnd = System.currentTimeMillis();

                    log.debug("[getLatestSeatResults] Seat lookup seatId={} took={} ms",
                            ipResult.getSeatId(), (seatEnd - seatStart));

                    return new IpResultDto.SeatStatusDto(seat.getSeatsNum(), ipResult.getResult());
                })
                .toList();
        long endSeatLookup = System.currentTimeMillis();

        long endTotal = System.currentTimeMillis();

        log.info("[getLatestSeatResults] Seat lookup total: {} ms", (endSeatLookup - startSeatLookup));
        log.info("[getLatestSeatResults] Total execution: {} ms", (endTotal - startTotal));

        return result;
    }


}