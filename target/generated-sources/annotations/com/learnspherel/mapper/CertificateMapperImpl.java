package com.learnspherel.mapper;

import com.learnspherel.dto.CertificateRequestDTO;
import com.learnspherel.dto.CertificateResponseDTO;
import com.learnspherel.entity.Certificate;
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
public class CertificateMapperImpl implements CertificateMapper {

    @Override
    public CertificateResponseDTO toResponseDto(Certificate entity) {
        if ( entity == null ) {
            return null;
        }

        CertificateResponseDTO certificateResponseDTO = new CertificateResponseDTO();

        certificateResponseDTO.setTenNguoiDung( entityNguoiDungTenNguoiDung( entity ) );
        certificateResponseDTO.setTenKhoaHoc( entityKhoaHocTieuDe( entity ) );
        certificateResponseDTO.setMaGiangVien( entityKhoaHocGiangVienMaNguoiDung( entity ) );
        certificateResponseDTO.setMaChungChi( entity.getMaChungChi() );
        certificateResponseDTO.setMaChungChiXacNhan( entity.getMaChungChiXacNhan() );
        certificateResponseDTO.setNguoiKy( entity.getNguoiKy() );
        certificateResponseDTO.setDiemCuoiKhoa( entity.getDiemCuoiKhoa() );
        certificateResponseDTO.setNgayCap( entity.getNgayCap() );
        certificateResponseDTO.setTrangThai( entity.getTrangThai() );

        return certificateResponseDTO;
    }

    @Override
    public Certificate toEntity(CertificateRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Certificate.CertificateBuilder certificate = Certificate.builder();

        certificate.diemCuoiKhoa( dto.getDiemCuoiKhoa() );
        certificate.nguoiKy( dto.getNguoiKy() );

        return certificate.build();
    }

    private String entityNguoiDungTenNguoiDung(Certificate certificate) {
        NguoiDung nguoiDung = certificate.getNguoiDung();
        if ( nguoiDung == null ) {
            return null;
        }
        return nguoiDung.getTenNguoiDung();
    }

    private String entityKhoaHocTieuDe(Certificate certificate) {
        KhoaHoc khoaHoc = certificate.getKhoaHoc();
        if ( khoaHoc == null ) {
            return null;
        }
        return khoaHoc.getTieuDe();
    }

    private Long entityKhoaHocGiangVienMaNguoiDung(Certificate certificate) {
        KhoaHoc khoaHoc = certificate.getKhoaHoc();
        if ( khoaHoc == null ) {
            return null;
        }
        NguoiDung giangVien = khoaHoc.getGiangVien();
        if ( giangVien == null ) {
            return null;
        }
        return giangVien.getMaNguoiDung();
    }
}
