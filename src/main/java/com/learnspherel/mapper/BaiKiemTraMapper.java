package com.learnspherel.mapper;

import com.learnspherel.dto.BaiKiemTraDto;
import com.learnspherel.entity.BaiKiemTra;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {CauHoiMapper.class})
public interface BaiKiemTraMapper {

    @Mappings({
            @Mapping(target = "baiHoc", ignore = true),
            @Mapping(target = "khoaHoc", ignore = true),
            @Mapping(target = "cauHois", ignore = true),
            @Mapping(target = "truongHopKiemThus", ignore = true),
            @Mapping(target = "ngayTao", ignore = true),
            @Mapping(target = "ngayCapNhat", ignore = true)
    })
    BaiKiemTra toEntity(BaiKiemTraDto baiKiemTraDto);

    @Mappings({
            @Mapping(source = "khoaHoc.maKhoaHoc", target = "maKhoaHoc"),
            @Mapping(source = "baiHoc.maBaiHoc", target = "maBaiHoc"),
            @Mapping(source = "cauHois", target = "cauHois")
    })
    BaiKiemTraDto toDto(BaiKiemTra baiKiemTra);
}