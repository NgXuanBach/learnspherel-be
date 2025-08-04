package com.learnspherel.mapper;

import com.learnspherel.dto.GioHangDto;
import com.learnspherel.entity.GioHang;
import com.learnspherel.entity.KhoaHoc;
import com.learnspherel.entity.NguoiDung;
import com.learnspherel.entity.enums.TrinhDo;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-22T20:04:51+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.15 (Amazon.com Inc.)"
)
@Component
public class GioHangMapperImpl implements GioHangMapper {

    @Override
    public GioHangDto toDto(GioHang gioHang) {
        if ( gioHang == null ) {
            return null;
        }

        GioHangDto.GioHangDtoBuilder gioHangDto = GioHangDto.builder();

        gioHangDto.maNguoiDung( gioHangNguoiDungMaNguoiDung( gioHang ) );
        gioHangDto.maKhoaHoc( gioHangKhoaHocMaKhoaHoc( gioHang ) );
        gioHangDto.tenKhoaHoc( gioHangKhoaHocTieuDe( gioHang ) );
        gioHangDto.trinhDo( gioHangKhoaHocTrinhDo( gioHang ) );
        gioHangDto.gia( gioHangKhoaHocGia( gioHang ) );
        gioHangDto.anhDaiDienKhoaHoc( gioHangKhoaHocAnhDaiDien( gioHang ) );
        gioHangDto.maGioHang( gioHang.getMaGioHang() );
        gioHangDto.ngayThem( gioHang.getNgayThem() );

        gioHangDto.trangThai( gioHang.getKhoaHoc().getCoPhi() ? "CÓ PHÍ" : "MIỄN PHÍ" );
        gioHangDto.coPhi( gioHang.getKhoaHoc().getCoPhi() );

        return gioHangDto.build();
    }

    @Override
    public GioHang toEntity(GioHangDto gioHangDto) {
        if ( gioHangDto == null ) {
            return null;
        }

        GioHang.GioHangBuilder gioHang = GioHang.builder();

        gioHang.nguoiDung( gioHangDtoToNguoiDung( gioHangDto ) );
        gioHang.khoaHoc( gioHangDtoToKhoaHoc( gioHangDto ) );
        gioHang.maGioHang( gioHangDto.getMaGioHang() );
        gioHang.ngayThem( gioHangDto.getNgayThem() );

        return gioHang.build();
    }

    private Long gioHangNguoiDungMaNguoiDung(GioHang gioHang) {
        NguoiDung nguoiDung = gioHang.getNguoiDung();
        if ( nguoiDung == null ) {
            return null;
        }
        return nguoiDung.getMaNguoiDung();
    }

    private Long gioHangKhoaHocMaKhoaHoc(GioHang gioHang) {
        KhoaHoc khoaHoc = gioHang.getKhoaHoc();
        if ( khoaHoc == null ) {
            return null;
        }
        return khoaHoc.getMaKhoaHoc();
    }

    private String gioHangKhoaHocTieuDe(GioHang gioHang) {
        KhoaHoc khoaHoc = gioHang.getKhoaHoc();
        if ( khoaHoc == null ) {
            return null;
        }
        return khoaHoc.getTieuDe();
    }

    private TrinhDo gioHangKhoaHocTrinhDo(GioHang gioHang) {
        KhoaHoc khoaHoc = gioHang.getKhoaHoc();
        if ( khoaHoc == null ) {
            return null;
        }
        return khoaHoc.getTrinhDo();
    }

    private Double gioHangKhoaHocGia(GioHang gioHang) {
        KhoaHoc khoaHoc = gioHang.getKhoaHoc();
        if ( khoaHoc == null ) {
            return null;
        }
        return khoaHoc.getGia();
    }

    private String gioHangKhoaHocAnhDaiDien(GioHang gioHang) {
        KhoaHoc khoaHoc = gioHang.getKhoaHoc();
        if ( khoaHoc == null ) {
            return null;
        }
        return khoaHoc.getAnhDaiDien();
    }

    protected NguoiDung gioHangDtoToNguoiDung(GioHangDto gioHangDto) {
        if ( gioHangDto == null ) {
            return null;
        }

        NguoiDung.NguoiDungBuilder nguoiDung = NguoiDung.builder();

        nguoiDung.maNguoiDung( gioHangDto.getMaNguoiDung() );

        return nguoiDung.build();
    }

    protected KhoaHoc gioHangDtoToKhoaHoc(GioHangDto gioHangDto) {
        if ( gioHangDto == null ) {
            return null;
        }

        KhoaHoc.KhoaHocBuilder khoaHoc = KhoaHoc.builder();

        khoaHoc.maKhoaHoc( gioHangDto.getMaKhoaHoc() );

        return khoaHoc.build();
    }
}
