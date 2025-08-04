package com.learnspherel.mapper;

import com.learnspherel.dto.CauHoiDto;
import com.learnspherel.entity.CauHoi;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-22T20:04:51+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.15 (Amazon.com Inc.)"
)
@Component
public class CauHoiMapperImpl implements CauHoiMapper {

    @Override
    public CauHoi toEntity(CauHoiDto cauHoiDto) {
        if ( cauHoiDto == null ) {
            return null;
        }

        CauHoi.CauHoiBuilder cauHoi = CauHoi.builder();

        cauHoi.maCauHoi( cauHoiDto.getMaCauHoi() );
        cauHoi.noiDung( cauHoiDto.getNoiDung() );
        cauHoi.dapAn( cauHoiDto.getDapAn() );

        return cauHoi.build();
    }

    @Override
    public CauHoiDto toDto(CauHoi cauHoi) {
        if ( cauHoi == null ) {
            return null;
        }

        CauHoiDto cauHoiDto = new CauHoiDto();

        cauHoiDto.setMaCauHoi( cauHoi.getMaCauHoi() );
        cauHoiDto.setNoiDung( cauHoi.getNoiDung() );
        cauHoiDto.setDapAn( cauHoi.getDapAn() );

        return cauHoiDto;
    }
}
