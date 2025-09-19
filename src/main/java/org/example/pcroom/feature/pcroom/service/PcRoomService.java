package org.example.pcroom.feature.pcroom.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.pcroom.dto.PcroomDto;
import org.example.pcroom.feature.pcroom.dto.PingUtilizationDto;
import org.example.pcroom.feature.pcroom.dto.SeatsDto;
import org.example.pcroom.feature.pcroom.entity.Pcroom;
import org.example.pcroom.feature.pcroom.entity.Seat;
import org.example.pcroom.feature.pcroom.repository.PcroomRepository;
import org.example.pcroom.feature.pcroom.repository.SeatRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PcRoomService {
    private final PingService pingService;
    private final PcroomRepository pcroomRepository;
    private final SeatRepository seatRepository;


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

        return new PingUtilizationDto(
                pcRoomId,
                name,
                utilization
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
}