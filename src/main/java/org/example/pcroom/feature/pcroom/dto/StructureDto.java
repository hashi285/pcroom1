package org.example.pcroom.feature.pcroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.pcroom.feature.pcroom.entity.Pcroom;
import org.example.pcroom.feature.pcroom.entity.PcroomStructure;
import org.example.pcroom.feature.pcroom.enums.StructureType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StructureDto {
    private String nameOfPcroom;
    
    @NotNull(message = "구조물 타입은 필수입니다.")
    private StructureType type;
    
    @Min(value = 0, message = "X 좌표는 음수가 될 수 없습니다.")
    private int x;
    
    @Min(value = 0, message = "Y 좌표는 음수가 될 수 없습니다.")
    private int y;
    
    @Min(value = 10, message = "너비는 최소 10 이상이어야 합니다.")
    @Max(value = 1000, message = "너비는 최대 1000 이하여야 합니다.")
    private int width;
    
    @Min(value = 10, message = "높이는 최소 10 이상이어야 합니다.")
    @Max(value = 1000, message = "높이는 최대 1000 이하여야 합니다.")
    private int height;

    public PcroomStructure toEntity(Pcroom pcroom) {
        PcroomStructure entity = new PcroomStructure();
        entity.setPcroomId(pcroom.getPcroomId());
        entity.setType(this.type);
        entity.setX(this.x);
        entity.setY(this.y);
        entity.setWidth(this.width);
        entity.setHeight(this.height);
        return entity;
    }
}
