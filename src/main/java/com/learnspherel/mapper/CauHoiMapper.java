package com.learnspherel.mapper;

import com.learnspherel.dto.CauHoiDto;
import com.learnspherel.entity.CauHoi;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CauHoiMapper {

    @Mappings({
            @Mapping(target = "baiKiemTra", ignore = true),
            @Mapping(target = "ngayTao", ignore = true)
    })
    CauHoi toEntity(CauHoiDto cauHoiDto);

    @Mappings({
            @Mapping(source = "maCauHoi", target = "maCauHoi"),
            @Mapping(source = "noiDung", target = "noiDung"),
            @Mapping(source = "dapAn", target = "dapAn")
    })
    CauHoiDto toDto(CauHoi cauHoi);
}