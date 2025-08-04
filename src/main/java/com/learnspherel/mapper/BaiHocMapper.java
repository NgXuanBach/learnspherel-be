package com.learnspherel.mapper;

import com.learnspherel.dto.BaiHocDto;
import com.learnspherel.dto.BaiHocResponse;
import com.learnspherel.entity.BaiHoc;
import com.learnspherel.entity.KhoaHoc;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {TaiLieuMapper.class})
public interface BaiHocMapper {

    @Mappings({
            @Mapping(source = "khoaHoc.maKhoaHoc", target = "maKhoaHoc"),
//            @Mapping(source = "taiLieus", target = "taiLieus")
    })
    BaiHocDto baiHocToBaiHocDto(BaiHoc baiHoc);

    @Mappings({
            @Mapping(source = "maKhoaHoc", target = "khoaHoc", qualifiedByName = "mapKhoaHoc"),
//            @Mapping(target = "taiLieus", ignore = true)
    })
    BaiHoc baiHocDtoToBaiHoc(BaiHocDto baiHocDto);

    @Mapping(source = "maBaiHoc", target = "maBaiHoc")
    @Mapping(source = "tieuDe", target = "tieuDe")
    @Mapping(source = "thuTuBaiHoc", target = "thuTuBai")
    BaiHocResponse toResponse(BaiHoc baiHoc);

    @Named("mapKhoaHoc")
    static KhoaHoc mapKhoaHoc(Long maKhoaHoc) {
        return maKhoaHoc != null ? KhoaHoc.builder().maKhoaHoc(maKhoaHoc).build() : null;
    }
}
