package org.example.pcroom.feature.pcroom.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.pcroom.dto.PcroomDto;
import org.example.pcroom.feature.pcroom.dto.SeatsDto;
import org.example.pcroom.feature.pcroom.entity.Pcroom;
import org.example.pcroom.feature.pcroom.service.PcRoomService;
import org.example.pcroom.feature.pcroom.service.PingService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequestMapping("pc")
@RestController
@RequiredArgsConstructor
public class PcRoomController {
    private final PcRoomService pcRoomService;
    private final PingService pingService;

    /**
     *
     * @param pcroomid
     * @return 컴퓨터 생존 여부를 list 형식으로 반환
     * @throws Exception
     */
    @GetMapping("/{pcroomid}")
    @Operation(summary = "컴퓨터 생존 여부", description = "컴퓨터 생존 여부를 list 형식으로 반환")
    public List<String> getPcroom(@PathVariable Long pcroomid) throws Exception {
        return  pingService.update(pcroomid);
    }


    @GetMapping("/pc/{pcroomid}")
    @Operation(summary = "피시방 가동률 확인", description = "피시방 가동률 반환")
    public String getSeats(@PathVariable Long pcroomid) throws Exception {
        return pcRoomService.canUseSeat(pcroomid);
    }

//    @GetMapping("/pc/{pcroomid}/{is_available}")
//    @Operation(summary = "피시방 다인 이용 자리 추천", description = "사용 가능 자리 Map 형식으로 반환")
//    public Map<Integer, List<Integer>> getPcRoomSeats(@PathVariable Long pcroomid, Long is_available) throws Exception {
//        return pcRoomService.getIpResult(pcroomid,is_available);
//    }

    // GET | http://수혁군서버.com/api/pcrooms/1 -> pc 상세조회
    // POST |http://수혁군서버.com/api/pcrooms/ -> pc 새로등록
    @PostMapping("/set_pcroom")
    @Operation(summary = "피시방 저장")
    public PcroomDto.ReadPcRoomResponse setPcroom(@RequestBody PcroomDto.CreatePcRoomRequest request) {

        return pcRoomService.registerNewPcroom(request);
    }

    @PostMapping
    @Operation(summary = "자리 저장")
    public List<SeatsDto> setSeats(@RequestBody SeatsDto seatsDto) {

        return pcRoomService.registerNewSeat(seatsDto);
    }
}