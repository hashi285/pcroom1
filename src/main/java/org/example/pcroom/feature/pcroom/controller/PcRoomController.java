package org.example.pcroom.feature.pcroom.controller;

import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.pcroom.service.PcRoomService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class PcRoomController {
    private final PcRoomService pcRoomService;

    @GetMapping("/hello")
    public String hello() {
        return "문혁수?!";
    }


}