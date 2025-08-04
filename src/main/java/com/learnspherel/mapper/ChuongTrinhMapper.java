package com.learnspherel.mapper;

import com.learnspherel.dto.ChuongHocResponse;
import com.learnspherel.dto.ChuongTrinhDto;
import com.learnspherel.entity.ChuongTrinh;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BaiHocMapper.class})
public interface ChuongTrinhMapper {
    @Mapping(source = "khoaHoc.maKhoaHoc", target = "maKhoaHoc")
    @Mapping(source = "baiHocs", target = "baiHocs")
    ChuongTrinhDto toDto(ChuongTrinh chuongTrinh);

    @Mapping(source = "maKhoaHoc", target = "khoaHoc.maKhoaHoc")
    @Mapping(source = "baiHocs", target = "baiHocs")
    ChuongTrinh toEntity(ChuongTrinhDto chuongTrinhDto);

    @Mapping(source = "maChuongTrinh", target = "maChuong")
    @Mapping(source = "tieuDe", target = "tenChuong")
    @Mapping(source = "thuTuChuong", target = "thuTuChuong")
    @Mapping(source = "baiHocs", target = "baiHoc")
    ChuongHocResponse toResponse(ChuongTrinh chuongTrinh);
}