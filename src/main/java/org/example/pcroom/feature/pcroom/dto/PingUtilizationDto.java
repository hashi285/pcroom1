package org.example.pcroom.feature.pcroom.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pcroom.feature.pcroom.entity.IpResult;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PingUtilizationDto {
    private Long pcroomId;
    private String nameOfPcroom;
    private double utilization;
    private Integer seatCount;
    private Integer usedSeatCount;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UtilizationAndResults{
        private List<IpResult> result;
        private Long pcroomId;
        private double utilization;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime now;
    }
}
