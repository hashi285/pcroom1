package org.example.pcroom.feature.pcroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.pcroom.feature.pcroom.entity.Pcroom;
import org.example.pcroom.feature.pcroom.entity.PcroomStructure;
import org.example.pcroom.feature.pcroom.enums.StructureType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StructureDto {
    private String nameOfPcroom;
    private StructureType type;
    private int x;
    private int y;
    private int width;
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
