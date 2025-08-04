package com.learnspherel.mapper;

import com.learnspherel.dto.TaiLieuDto;
import com.learnspherel.entity.BaiHoc;
import com.learnspherel.entity.BaiNop;
import com.learnspherel.entity.TaiLieu;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-22T20:04:51+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.15 (Amazon.com Inc.)"
)
@Component
public class TaiLieuMapperImpl implements TaiLieuMapper {

    @Override
    public TaiLieuDto taiLieuToTaiLieuDto(TaiLieu taiLieu) {
        if ( taiLieu == null ) {
            return null;
        }

        TaiLieuDto.TaiLieuDtoBuilder taiLieuDto = TaiLieuDto.builder();

        taiLieuDto.maBaiHoc( taiLieuBaiHocMaBaiHoc( taiLieu ) );
        taiLieuDto.maBaiNop( taiLieuBaiNopMaBaiNop( taiLieu ) );
        taiLieuDto.loaiFile( TaiLieuMapper.enumToString( taiLieu.getLoaiFile() ) );
        taiLieuDto.maTaiLieu( taiLieu.getMaTaiLieu() );
        taiLieuDto.tenFile( taiLieu.getTenFile() );
        taiLieuDto.duongDan( taiLieu.getDuongDan() );
        taiLieuDto.kichThuoc( taiLieu.getKichThuoc() );
        taiLieuDto.ngayTaiLen( taiLieu.getNgayTaiLen() );

        return taiLieuDto.build();
    }

    @Override
    public TaiLieu taiLieuDtoToTaiLieu(TaiLieuDto dto) {
        if ( dto == null ) {
            return null;
        }

        TaiLieu.TaiLieuBuilder taiLieu = TaiLieu.builder();

        taiLieu.baiHoc( TaiLieuMapper.mapBaiHoc( dto.getMaBaiHoc() ) );
        taiLieu.baiNop( TaiLieuMapper.mapBaiNop( dto.getMaBaiNop() ) );
        taiLieu.loaiFile( TaiLieuMapper.stringToEnum( dto.getLoaiFile() ) );
        taiLieu.maTaiLieu( dto.getMaTaiLieu() );
        taiLieu.tenFile( dto.getTenFile() );
        taiLieu.duongDan( dto.getDuongDan() );
        taiLieu.kichThuoc( dto.getKichThuoc() );
        taiLieu.ngayTaiLen( dto.getNgayTaiLen() );

        return taiLieu.build();
    }

    private Long taiLieuBaiHocMaBaiHoc(TaiLieu taiLieu) {
        BaiHoc baiHoc = taiLieu.getBaiHoc();
        if ( baiHoc == null ) {
            return null;
        }
        return baiHoc.getMaBaiHoc();
    }

    private Long taiLieuBaiNopMaBaiNop(TaiLieu taiLieu) {
        BaiNop baiNop = taiLieu.getBaiNop();
        if ( baiNop == null ) {
            return null;
        }
        return baiNop.getMaBaiNop();
    }
}
