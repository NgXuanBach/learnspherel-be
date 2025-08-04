package com.learnspherel.mapper;

import com.learnspherel.dto.DangKyKhoaHocResponse;
import com.learnspherel.entity.DangKyKhoaHoc;
import com.learnspherel.entity.GiaoDich;
import com.learnspherel.entity.KhoaHoc;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-22T20:04:51+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.15 (Amazon.com Inc.)"
)
@Component
public class DangKyKhoaHocMapperImpl implements DangKyKhoaHocMapper {

    @Override
    public DangKyKhoaHocResponse toDto(DangKyKhoaHoc dangKy, GiaoDich giaoDich) {
        if ( dangKy == null && giaoDich == null ) {
            return null;
        }

        DangKyKhoaHocResponse.DangKyKhoaHocResponseBuilder dangKyKhoaHocResponse = DangKyKhoaHocResponse.builder();

        if ( dangKy != null ) {
            dangKyKhoaHocResponse.maDangKy( dangKy.getMaDangKy() );
            dangKyKhoaHocResponse.maKhoaHoc( dangKyKhoaHocMaKhoaHoc( dangKy ) );
            dangKyKhoaHocResponse.tieuDe( dangKyKhoaHocTieuDe( dangKy ) );
            dangKyKhoaHocResponse.anhDaiDien( dangKyKhoaHocAnhDaiDien( dangKy ) );
            dangKyKhoaHocResponse.gia( dangKyKhoaHocGia( dangKy ) );
            dangKyKhoaHocResponse.coPhi( dangKyKhoaHocCoPhi( dangKy ) );
            dangKyKhoaHocResponse.trangThaiThanhToan( dangKy.getTrangThaiThanhToan() );
        }
        if ( giaoDich != null ) {
            dangKyKhoaHocResponse.maGiaoDich( giaoDich.getMaGiaoDich() );
            dangKyKhoaHocResponse.soTien( giaoDich.getSoTien() );
            dangKyKhoaHocResponse.phuongThuc( giaoDich.getPhuongThuc() );
            dangKyKhoaHocResponse.maGiaoDichNganHang( giaoDich.getMaGiaoDichNganHang() );
            dangKyKhoaHocResponse.trangThai( giaoDich.getTrangThai() );
            dangKyKhoaHocResponse.ngayGiaoDich( giaoDich.getNgayGiaoDich() );
        }

        return dangKyKhoaHocResponse.build();
    }

    private Long dangKyKhoaHocMaKhoaHoc(DangKyKhoaHoc dangKyKhoaHoc) {
        KhoaHoc khoaHoc = dangKyKhoaHoc.getKhoaHoc();
        if ( khoaHoc == null ) {
            return null;
        }
        return khoaHoc.getMaKhoaHoc();
    }

    private String dangKyKhoaHocTieuDe(DangKyKhoaHoc dangKyKhoaHoc) {
        KhoaHoc khoaHoc = dangKyKhoaHoc.getKhoaHoc();
        if ( khoaHoc == null ) {
            return null;
        }
        return khoaHoc.getTieuDe();
    }

    private String dangKyKhoaHocAnhDaiDien(DangKyKhoaHoc dangKyKhoaHoc) {
        KhoaHoc khoaHoc = dangKyKhoaHoc.getKhoaHoc();
        if ( khoaHoc == null ) {
            return null;
        }
        return khoaHoc.getAnhDaiDien();
    }

    private Double dangKyKhoaHocGia(DangKyKhoaHoc dangKyKhoaHoc) {
        KhoaHoc khoaHoc = dangKyKhoaHoc.getKhoaHoc();
        if ( khoaHoc == null ) {
            return null;
        }
        return khoaHoc.getGia();
    }

    private Boolean dangKyKhoaHocCoPhi(DangKyKhoaHoc dangKyKhoaHoc) {
        KhoaHoc khoaHoc = dangKyKhoaHoc.getKhoaHoc();
        if ( khoaHoc == null ) {
            return null;
        }
        return khoaHoc.getCoPhi();
    }
}
