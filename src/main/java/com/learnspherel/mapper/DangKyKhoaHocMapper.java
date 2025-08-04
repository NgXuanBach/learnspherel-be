package com.learnspherel.mapper;

import com.learnspherel.dto.DangKyKhoaHocResponse;
import com.learnspherel.entity.DangKyKhoaHoc;
import com.learnspherel.entity.GiaoDich;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DangKyKhoaHocMapper {
    @Mapping(source = "dangKy.maDangKy", target = "maDangKy")
    @Mapping(source = "dangKy.khoaHoc.maKhoaHoc", target = "maKhoaHoc")
    @Mapping(source = "dangKy.khoaHoc.tieuDe", target = "tieuDe")
    @Mapping(source = "dangKy.khoaHoc.anhDaiDien", target = "anhDaiDien")
    @Mapping(source = "dangKy.khoaHoc.gia", target = "gia")
    @Mapping(source = "dangKy.khoaHoc.coPhi", target = "coPhi")
    @Mapping(source = "dangKy.trangThaiThanhToan", target = "trangThaiThanhToan")
    @Mapping(source = "giaoDich.maGiaoDich", target = "maGiaoDich")
    @Mapping(source = "giaoDich.soTien", target = "soTien")
    @Mapping(source = "giaoDich.phuongThuc", target = "phuongThuc")
    @Mapping(source = "giaoDich.maGiaoDichNganHang", target = "maGiaoDichNganHang")
    @Mapping(source = "giaoDich.trangThai", target = "trangThai")
    @Mapping(source = "giaoDich.ngayGiaoDich", target = "ngayGiaoDich")
    DangKyKhoaHocResponse toDto(DangKyKhoaHoc dangKy, GiaoDich giaoDich);
}