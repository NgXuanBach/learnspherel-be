package com.learnspherel.mapper;

import com.learnspherel.dto.GioHangDto;
import com.learnspherel.entity.GioHang;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GioHangMapper {

    @Mapping(source = "nguoiDung.maNguoiDung", target = "maNguoiDung")
    @Mapping(source = "khoaHoc.maKhoaHoc", target = "maKhoaHoc")
    @Mapping(source = "khoaHoc.tieuDe", target = "tenKhoaHoc")
    @Mapping(source = "khoaHoc.trinhDo", target = "trinhDo")
    @Mapping(source = "khoaHoc.gia", target = "gia")
    @Mapping(expression = "java(gioHang.getKhoaHoc().getCoPhi() ? \"CÓ PHÍ\" : \"MIỄN PHÍ\")", target = "trangThai")
    @Mapping(expression = "java(gioHang.getKhoaHoc().getCoPhi())", target = "coPhi")
    @Mapping(source = "khoaHoc.anhDaiDien", target = "anhDaiDienKhoaHoc")
    GioHangDto toDto(GioHang gioHang);

    @Mapping(source = "maNguoiDung", target = "nguoiDung.maNguoiDung")
    @Mapping(source = "maKhoaHoc", target = "khoaHoc.maKhoaHoc")
    GioHang toEntity(GioHangDto gioHangDto);

}