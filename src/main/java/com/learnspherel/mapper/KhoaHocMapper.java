package com.learnspherel.mapper;

import com.learnspherel.dto.KhoaHocRequest;
import com.learnspherel.dto.KhoaHocResponse;
import com.learnspherel.entity.KhoaHoc;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface KhoaHocMapper {

    @Mappings({
            @Mapping(target = "giangVien", ignore = true),
            @Mapping(target = "kyNangs", ignore = true),
            @Mapping(target = "baiHocs", ignore = true),
            @Mapping(target = "chuongTrinhs", ignore = true),
            @Mapping(target = "dangKyKhoaHocs", ignore = true),
            @Mapping(target = "gioHangs", ignore = true),
            @Mapping(target = "ngonNgu", ignore = true),
            @Mapping(target = "thoiHan", ignore = true),
            @Mapping(target = "chungChi", ignore = true)
    })
    KhoaHoc toEntity(KhoaHocRequest request);

    @Mappings({
            @Mapping(source = "giangVien.maNguoiDung", target = "maGiangVien"),
            @Mapping(source = "giangVien.tenNguoiDung", target = "tenGiangVien"),
            @Mapping(source = "giangVien.anhDaiDien", target = "anhDaiDienGiangVien"),
            @Mapping(source = "videoDemoUrl", target = "videoDemoUrl"),
//            @Mapping(expression = "java(khoaHoc.getKyNangs().stream().map(com.learnspherel.entity.KyNang::getMaKyNang).collect(java.util.stream.Collectors.toList()))", target = "kyNangIds"),
            @Mapping(target = "tongThoiLuong", ignore = true),
            @Mapping(target = "soBaiGiang", ignore = true),
            @Mapping(target = "soNguoiThamGia", ignore = true),
            @Mapping(target = "diemDanhGiaKhoaHoc", ignore = true),
            @Mapping(target = "soDanhGiaKhoaHoc", ignore = true),
            @Mapping(target = "diemDanhGiaGiangVien", ignore = true),
            @Mapping(target = "soDanhGiaGiangVien", ignore = true),
            @Mapping(target = "soKhoaHocGiangVien", ignore = true),
            @Mapping(target = "soHocVienGiangVien", ignore = true)
    })
    KhoaHocResponse toResponse(KhoaHoc khoaHoc);
}