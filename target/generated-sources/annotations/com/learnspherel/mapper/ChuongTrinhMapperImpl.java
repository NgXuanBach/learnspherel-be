package com.learnspherel.mapper;

import com.learnspherel.dto.BaiHocDto;
import com.learnspherel.dto.BaiHocResponse;
import com.learnspherel.dto.ChuongHocResponse;
import com.learnspherel.dto.ChuongTrinhDto;
import com.learnspherel.entity.BaiHoc;
import com.learnspherel.entity.ChuongTrinh;
import com.learnspherel.entity.KhoaHoc;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-22T20:04:51+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.15 (Amazon.com Inc.)"
)
@Component
public class ChuongTrinhMapperImpl implements ChuongTrinhMapper {

    @Autowired
    private BaiHocMapper baiHocMapper;

    @Override
    public ChuongTrinhDto toDto(ChuongTrinh chuongTrinh) {
        if ( chuongTrinh == null ) {
            return null;
        }

        ChuongTrinhDto.ChuongTrinhDtoBuilder chuongTrinhDto = ChuongTrinhDto.builder();

        chuongTrinhDto.maKhoaHoc( chuongTrinhKhoaHocMaKhoaHoc( chuongTrinh ) );
        chuongTrinhDto.baiHocs( baiHocListToBaiHocDtoList( chuongTrinh.getBaiHocs() ) );
        chuongTrinhDto.maChuongTrinh( chuongTrinh.getMaChuongTrinh() );
        chuongTrinhDto.tieuDe( chuongTrinh.getTieuDe() );
        chuongTrinhDto.thuTuChuong( chuongTrinh.getThuTuChuong() );
        chuongTrinhDto.moTa( chuongTrinh.getMoTa() );
        chuongTrinhDto.ngayTao( chuongTrinh.getNgayTao() );
        chuongTrinhDto.ngayCapNhat( chuongTrinh.getNgayCapNhat() );

        return chuongTrinhDto.build();
    }

    @Override
    public ChuongTrinh toEntity(ChuongTrinhDto chuongTrinhDto) {
        if ( chuongTrinhDto == null ) {
            return null;
        }

        ChuongTrinh.ChuongTrinhBuilder chuongTrinh = ChuongTrinh.builder();

        chuongTrinh.khoaHoc( chuongTrinhDtoToKhoaHoc( chuongTrinhDto ) );
        chuongTrinh.baiHocs( baiHocDtoListToBaiHocList( chuongTrinhDto.getBaiHocs() ) );
        chuongTrinh.maChuongTrinh( chuongTrinhDto.getMaChuongTrinh() );
        chuongTrinh.tieuDe( chuongTrinhDto.getTieuDe() );
        chuongTrinh.thuTuChuong( chuongTrinhDto.getThuTuChuong() );
        chuongTrinh.moTa( chuongTrinhDto.getMoTa() );
        chuongTrinh.ngayTao( chuongTrinhDto.getNgayTao() );
        chuongTrinh.ngayCapNhat( chuongTrinhDto.getNgayCapNhat() );

        return chuongTrinh.build();
    }

    @Override
    public ChuongHocResponse toResponse(ChuongTrinh chuongTrinh) {
        if ( chuongTrinh == null ) {
            return null;
        }

        ChuongHocResponse chuongHocResponse = new ChuongHocResponse();

        chuongHocResponse.setMaChuong( chuongTrinh.getMaChuongTrinh() );
        chuongHocResponse.setTenChuong( chuongTrinh.getTieuDe() );
        chuongHocResponse.setThuTuChuong( chuongTrinh.getThuTuChuong() );
        chuongHocResponse.setBaiHoc( baiHocListToBaiHocResponseList( chuongTrinh.getBaiHocs() ) );

        return chuongHocResponse;
    }

    private Long chuongTrinhKhoaHocMaKhoaHoc(ChuongTrinh chuongTrinh) {
        KhoaHoc khoaHoc = chuongTrinh.getKhoaHoc();
        if ( khoaHoc == null ) {
            return null;
        }
        return khoaHoc.getMaKhoaHoc();
    }

    protected List<BaiHocDto> baiHocListToBaiHocDtoList(List<BaiHoc> list) {
        if ( list == null ) {
            return null;
        }

        List<BaiHocDto> list1 = new ArrayList<BaiHocDto>( list.size() );
        for ( BaiHoc baiHoc : list ) {
            list1.add( baiHocMapper.baiHocToBaiHocDto( baiHoc ) );
        }

        return list1;
    }

    protected KhoaHoc chuongTrinhDtoToKhoaHoc(ChuongTrinhDto chuongTrinhDto) {
        if ( chuongTrinhDto == null ) {
            return null;
        }

        KhoaHoc.KhoaHocBuilder khoaHoc = KhoaHoc.builder();

        khoaHoc.maKhoaHoc( chuongTrinhDto.getMaKhoaHoc() );

        return khoaHoc.build();
    }

    protected List<BaiHoc> baiHocDtoListToBaiHocList(List<BaiHocDto> list) {
        if ( list == null ) {
            return null;
        }

        List<BaiHoc> list1 = new ArrayList<BaiHoc>( list.size() );
        for ( BaiHocDto baiHocDto : list ) {
            list1.add( baiHocMapper.baiHocDtoToBaiHoc( baiHocDto ) );
        }

        return list1;
    }

    protected List<BaiHocResponse> baiHocListToBaiHocResponseList(List<BaiHoc> list) {
        if ( list == null ) {
            return null;
        }

        List<BaiHocResponse> list1 = new ArrayList<BaiHocResponse>( list.size() );
        for ( BaiHoc baiHoc : list ) {
            list1.add( baiHocMapper.toResponse( baiHoc ) );
        }

        return list1;
    }
}
