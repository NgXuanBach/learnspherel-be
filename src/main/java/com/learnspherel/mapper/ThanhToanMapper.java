        package com.learnspherel.mapper;

import com.learnspherel.dto.ThanhToanDto;
import com.learnspherel.entity.GiaoDich;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ThanhToanMapper {
    @Mapping(source = "dangKy.maDangKy", target = "maDangKy")
    @Mapping(source = "soTien", target = "soTien")
    @Mapping(source = "phuongThuc", target = "phuongThuc")
    @Mapping(source = "maGiaoDichNganHang", target = "maGiaoDichNganHang")
    @Mapping(source = "trangThai", target = "trangThai")
    @Mapping(source = "ngayGiaoDich", target = "ngayGiaoDich")
    @Mapping(source = "dangKy.khoaHoc", target = "khoaHoc")
    @Mapping(source = "dangKy.trangThaiThanhToan", target = "trangThaiThanhToan")
    ThanhToanDto toDto(GiaoDich giaoDich);

    @Mapping(source = "maDangKy", target = "dangKy.maDangKy")
    GiaoDich toEntity(ThanhToanDto thanhToanDto);
}