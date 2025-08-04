package com.learnspherel.mapper;

import com.learnspherel.dto.BaiHocDto;
import com.learnspherel.dto.BaiHocResponse;
import com.learnspherel.entity.BaiHoc;
import com.learnspherel.entity.KhoaHoc;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-22T20:04:51+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.15 (Amazon.com Inc.)"
)
@Component
public class BaiHocMapperImpl implements BaiHocMapper {

    @Override
    public BaiHocDto baiHocToBaiHocDto(BaiHoc baiHoc) {
        if ( baiHoc == null ) {
            return null;
        }

        BaiHocDto.BaiHocDtoBuilder baiHocDto = BaiHocDto.builder();

        baiHocDto.maKhoaHoc( baiHocKhoaHocMaKhoaHoc( baiHoc ) );
        baiHocDto.maBaiHoc( baiHoc.getMaBaiHoc() );
        baiHocDto.tieuDe( baiHoc.getTieuDe() );
        baiHocDto.thuTuBaiHoc( baiHoc.getThuTuBaiHoc() );
        baiHocDto.videoUrl( baiHoc.getVideoUrl() );
        baiHocDto.ngayTao( baiHoc.getNgayTao() );
        baiHocDto.ngayCapNhat( baiHoc.getNgayCapNhat() );

        return baiHocDto.build();
    }

    @Override
    public BaiHoc baiHocDtoToBaiHoc(BaiHocDto baiHocDto) {
        if ( baiHocDto == null ) {
            return null;
        }

        BaiHoc.BaiHocBuilder baiHoc = BaiHoc.builder();

        baiHoc.khoaHoc( BaiHocMapper.mapKhoaHoc( baiHocDto.getMaKhoaHoc() ) );
        baiHoc.maBaiHoc( baiHocDto.getMaBaiHoc() );
        baiHoc.tieuDe( baiHocDto.getTieuDe() );
        baiHoc.thuTuBaiHoc( baiHocDto.getThuTuBaiHoc() );
        baiHoc.videoUrl( baiHocDto.getVideoUrl() );
        baiHoc.ngayTao( baiHocDto.getNgayTao() );
        baiHoc.ngayCapNhat( baiHocDto.getNgayCapNhat() );

        return baiHoc.build();
    }

    @Override
    public BaiHocResponse toResponse(BaiHoc baiHoc) {
        if ( baiHoc == null ) {
            return null;
        }

        BaiHocResponse baiHocResponse = new BaiHocResponse();

        baiHocResponse.setMaBaiHoc( baiHoc.getMaBaiHoc() );
        baiHocResponse.setTieuDe( baiHoc.getTieuDe() );
        baiHocResponse.setThuTuBai( baiHoc.getThuTuBaiHoc() );
        baiHocResponse.setVideoUrl( baiHoc.getVideoUrl() );
        if ( baiHoc.getThoiLuong() != null ) {
            baiHocResponse.setThoiLuong( String.valueOf( baiHoc.getThoiLuong() ) );
        }

        return baiHocResponse;
    }

    private Long baiHocKhoaHocMaKhoaHoc(BaiHoc baiHoc) {
        KhoaHoc khoaHoc = baiHoc.getKhoaHoc();
        if ( khoaHoc == null ) {
            return null;
        }
        return khoaHoc.getMaKhoaHoc();
    }
}
