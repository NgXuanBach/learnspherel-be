package com.learnspherel.mapper;

import com.learnspherel.dto.KhoaHocRequest;
import com.learnspherel.dto.KhoaHocResponse;
import com.learnspherel.entity.KhoaHoc;
import com.learnspherel.entity.NguoiDung;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-22T20:04:51+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.15 (Amazon.com Inc.)"
)
@Component
public class KhoaHocMapperImpl implements KhoaHocMapper {

    @Override
    public KhoaHoc toEntity(KhoaHocRequest request) {
        if ( request == null ) {
            return null;
        }

        KhoaHoc.KhoaHocBuilder khoaHoc = KhoaHoc.builder();

        khoaHoc.tieuDe( request.getTieuDe() );
        khoaHoc.moTa( request.getMoTa() );
        khoaHoc.anhDaiDien( request.getAnhDaiDien() );
        khoaHoc.trinhDo( request.getTrinhDo() );
        khoaHoc.coPhi( request.getCoPhi() );
        khoaHoc.gia( request.getGia() );
        khoaHoc.videoDemoUrl( request.getVideoDemoUrl() );

        return khoaHoc.build();
    }

    @Override
    public KhoaHocResponse toResponse(KhoaHoc khoaHoc) {
        if ( khoaHoc == null ) {
            return null;
        }

        KhoaHocResponse khoaHocResponse = new KhoaHocResponse();

        Long maNguoiDung = khoaHocGiangVienMaNguoiDung( khoaHoc );
        if ( maNguoiDung != null ) {
            khoaHocResponse.setMaGiangVien( String.valueOf( maNguoiDung ) );
        }
        khoaHocResponse.setTenGiangVien( khoaHocGiangVienTenNguoiDung( khoaHoc ) );
        khoaHocResponse.setAnhDaiDienGiangVien( khoaHocGiangVienAnhDaiDien( khoaHoc ) );
        khoaHocResponse.setVideoDemoUrl( khoaHoc.getVideoDemoUrl() );
        khoaHocResponse.setMaKhoaHoc( khoaHoc.getMaKhoaHoc() );
        khoaHocResponse.setTieuDe( khoaHoc.getTieuDe() );
        khoaHocResponse.setMoTa( khoaHoc.getMoTa() );
        khoaHocResponse.setAnhDaiDien( khoaHoc.getAnhDaiDien() );
        khoaHocResponse.setTrinhDo( khoaHoc.getTrinhDo() );
        khoaHocResponse.setCoPhi( khoaHoc.getCoPhi() );
        khoaHocResponse.setGia( khoaHoc.getGia() );
        khoaHocResponse.setNgonNgu( khoaHoc.getNgonNgu() );
        khoaHocResponse.setThoiHan( khoaHoc.getThoiHan() );
        khoaHocResponse.setChungChi( khoaHoc.getChungChi() );
        khoaHocResponse.setNgayTao( khoaHoc.getNgayTao() );
        khoaHocResponse.setNgayCapNhat( khoaHoc.getNgayCapNhat() );

        return khoaHocResponse;
    }

    private Long khoaHocGiangVienMaNguoiDung(KhoaHoc khoaHoc) {
        NguoiDung giangVien = khoaHoc.getGiangVien();
        if ( giangVien == null ) {
            return null;
        }
        return giangVien.getMaNguoiDung();
    }

    private String khoaHocGiangVienTenNguoiDung(KhoaHoc khoaHoc) {
        NguoiDung giangVien = khoaHoc.getGiangVien();
        if ( giangVien == null ) {
            return null;
        }
        return giangVien.getTenNguoiDung();
    }

    private String khoaHocGiangVienAnhDaiDien(KhoaHoc khoaHoc) {
        NguoiDung giangVien = khoaHoc.getGiangVien();
        if ( giangVien == null ) {
            return null;
        }
        return giangVien.getAnhDaiDien();
    }
}
