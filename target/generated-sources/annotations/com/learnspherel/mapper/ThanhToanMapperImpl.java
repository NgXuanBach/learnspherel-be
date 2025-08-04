package com.learnspherel.mapper;

import com.learnspherel.dto.KhoaHocDto;
import com.learnspherel.dto.ThanhToanDto;
import com.learnspherel.entity.DangKyKhoaHoc;
import com.learnspherel.entity.GiaoDich;
import com.learnspherel.entity.KhoaHoc;
import com.learnspherel.entity.enums.TrangThaiThanhToan;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-22T20:04:51+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.15 (Amazon.com Inc.)"
)
@Component
public class ThanhToanMapperImpl implements ThanhToanMapper {

    @Override
    public ThanhToanDto toDto(GiaoDich giaoDich) {
        if ( giaoDich == null ) {
            return null;
        }

        ThanhToanDto.ThanhToanDtoBuilder thanhToanDto = ThanhToanDto.builder();

        thanhToanDto.maDangKy( giaoDichDangKyMaDangKy( giaoDich ) );
        thanhToanDto.soTien( giaoDich.getSoTien() );
        thanhToanDto.phuongThuc( giaoDich.getPhuongThuc() );
        thanhToanDto.maGiaoDichNganHang( giaoDich.getMaGiaoDichNganHang() );
        thanhToanDto.trangThai( giaoDich.getTrangThai() );
        thanhToanDto.ngayGiaoDich( giaoDich.getNgayGiaoDich() );
        thanhToanDto.khoaHoc( khoaHocToKhoaHocDto( giaoDichDangKyKhoaHoc( giaoDich ) ) );
        thanhToanDto.trangThaiThanhToan( giaoDichDangKyTrangThaiThanhToan( giaoDich ) );
        thanhToanDto.maGiaoDich( giaoDich.getMaGiaoDich() );

        return thanhToanDto.build();
    }

    @Override
    public GiaoDich toEntity(ThanhToanDto thanhToanDto) {
        if ( thanhToanDto == null ) {
            return null;
        }

        GiaoDich.GiaoDichBuilder giaoDich = GiaoDich.builder();

        giaoDich.dangKy( thanhToanDtoToDangKyKhoaHoc( thanhToanDto ) );
        giaoDich.maGiaoDich( thanhToanDto.getMaGiaoDich() );
        giaoDich.soTien( thanhToanDto.getSoTien() );
        giaoDich.phuongThuc( thanhToanDto.getPhuongThuc() );
        giaoDich.maGiaoDichNganHang( thanhToanDto.getMaGiaoDichNganHang() );
        giaoDich.trangThai( thanhToanDto.getTrangThai() );
        giaoDich.ngayGiaoDich( thanhToanDto.getNgayGiaoDich() );

        return giaoDich.build();
    }

    private Long giaoDichDangKyMaDangKy(GiaoDich giaoDich) {
        DangKyKhoaHoc dangKy = giaoDich.getDangKy();
        if ( dangKy == null ) {
            return null;
        }
        return dangKy.getMaDangKy();
    }

    private KhoaHoc giaoDichDangKyKhoaHoc(GiaoDich giaoDich) {
        DangKyKhoaHoc dangKy = giaoDich.getDangKy();
        if ( dangKy == null ) {
            return null;
        }
        return dangKy.getKhoaHoc();
    }

    protected KhoaHocDto khoaHocToKhoaHocDto(KhoaHoc khoaHoc) {
        if ( khoaHoc == null ) {
            return null;
        }

        KhoaHocDto.KhoaHocDtoBuilder khoaHocDto = KhoaHocDto.builder();

        khoaHocDto.maKhoaHoc( khoaHoc.getMaKhoaHoc() );
        khoaHocDto.tieuDe( khoaHoc.getTieuDe() );
        khoaHocDto.moTa( khoaHoc.getMoTa() );
        khoaHocDto.trinhDo( khoaHoc.getTrinhDo() );
        khoaHocDto.coPhi( khoaHoc.getCoPhi() );
        khoaHocDto.gia( khoaHoc.getGia() );
        khoaHocDto.anhDaiDien( khoaHoc.getAnhDaiDien() );
        khoaHocDto.thoiHan( khoaHoc.getThoiHan() );
        khoaHocDto.videoDemoUrl( khoaHoc.getVideoDemoUrl() );
        khoaHocDto.chungChi( khoaHoc.getChungChi() );
        khoaHocDto.ngayTao( khoaHoc.getNgayTao() );
        khoaHocDto.ngayCapNhat( khoaHoc.getNgayCapNhat() );

        return khoaHocDto.build();
    }

    private TrangThaiThanhToan giaoDichDangKyTrangThaiThanhToan(GiaoDich giaoDich) {
        DangKyKhoaHoc dangKy = giaoDich.getDangKy();
        if ( dangKy == null ) {
            return null;
        }
        return dangKy.getTrangThaiThanhToan();
    }

    protected DangKyKhoaHoc thanhToanDtoToDangKyKhoaHoc(ThanhToanDto thanhToanDto) {
        if ( thanhToanDto == null ) {
            return null;
        }

        DangKyKhoaHoc.DangKyKhoaHocBuilder dangKyKhoaHoc = DangKyKhoaHoc.builder();

        dangKyKhoaHoc.maDangKy( thanhToanDto.getMaDangKy() );

        return dangKyKhoaHoc.build();
    }
}
