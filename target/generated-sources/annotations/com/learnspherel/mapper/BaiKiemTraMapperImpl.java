package com.learnspherel.mapper;

import com.learnspherel.dto.BaiKiemTraDto;
import com.learnspherel.dto.CauHoiDto;
import com.learnspherel.entity.BaiHoc;
import com.learnspherel.entity.BaiKiemTra;
import com.learnspherel.entity.CauHoi;
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
public class BaiKiemTraMapperImpl implements BaiKiemTraMapper {

    @Autowired
    private CauHoiMapper cauHoiMapper;

    @Override
    public BaiKiemTra toEntity(BaiKiemTraDto baiKiemTraDto) {
        if ( baiKiemTraDto == null ) {
            return null;
        }

        BaiKiemTra.BaiKiemTraBuilder baiKiemTra = BaiKiemTra.builder();

        baiKiemTra.maBaiKiemTra( baiKiemTraDto.getMaBaiKiemTra() );
        baiKiemTra.tieuDe( baiKiemTraDto.getTieuDe() );
        baiKiemTra.diemDat( baiKiemTraDto.getDiemDat() );
        baiKiemTra.loai( baiKiemTraDto.getLoai() );

        return baiKiemTra.build();
    }

    @Override
    public BaiKiemTraDto toDto(BaiKiemTra baiKiemTra) {
        if ( baiKiemTra == null ) {
            return null;
        }

        BaiKiemTraDto baiKiemTraDto = new BaiKiemTraDto();

        baiKiemTraDto.setMaKhoaHoc( baiKiemTraKhoaHocMaKhoaHoc( baiKiemTra ) );
        baiKiemTraDto.setMaBaiHoc( baiKiemTraBaiHocMaBaiHoc( baiKiemTra ) );
        baiKiemTraDto.setCauHois( cauHoiListToCauHoiDtoList( baiKiemTra.getCauHois() ) );
        baiKiemTraDto.setMaBaiKiemTra( baiKiemTra.getMaBaiKiemTra() );
        baiKiemTraDto.setTieuDe( baiKiemTra.getTieuDe() );
        baiKiemTraDto.setDiemDat( baiKiemTra.getDiemDat() );
        baiKiemTraDto.setLoai( baiKiemTra.getLoai() );

        return baiKiemTraDto;
    }

    private Long baiKiemTraKhoaHocMaKhoaHoc(BaiKiemTra baiKiemTra) {
        KhoaHoc khoaHoc = baiKiemTra.getKhoaHoc();
        if ( khoaHoc == null ) {
            return null;
        }
        return khoaHoc.getMaKhoaHoc();
    }

    private Long baiKiemTraBaiHocMaBaiHoc(BaiKiemTra baiKiemTra) {
        BaiHoc baiHoc = baiKiemTra.getBaiHoc();
        if ( baiHoc == null ) {
            return null;
        }
        return baiHoc.getMaBaiHoc();
    }

    protected List<CauHoiDto> cauHoiListToCauHoiDtoList(List<CauHoi> list) {
        if ( list == null ) {
            return null;
        }

        List<CauHoiDto> list1 = new ArrayList<CauHoiDto>( list.size() );
        for ( CauHoi cauHoi : list ) {
            list1.add( cauHoiMapper.toDto( cauHoi ) );
        }

        return list1;
    }
}
