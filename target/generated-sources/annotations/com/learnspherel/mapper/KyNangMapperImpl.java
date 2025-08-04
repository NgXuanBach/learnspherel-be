package com.learnspherel.mapper;

import com.learnspherel.dto.KyNangRequest;
import com.learnspherel.dto.KyNangResponse;
import com.learnspherel.entity.KyNang;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-22T20:04:51+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.15 (Amazon.com Inc.)"
)
@Component
public class KyNangMapperImpl implements KyNangMapper {

    @Override
    public KyNang toEntity(KyNangRequest request) {
        if ( request == null ) {
            return null;
        }

        KyNang.KyNangBuilder kyNang = KyNang.builder();

        kyNang.tenKyNang( request.getTenKyNang() );
        kyNang.moTa( request.getMoTa() );

        return kyNang.build();
    }

    @Override
    public KyNangResponse toResponse(KyNang kyNang) {
        if ( kyNang == null ) {
            return null;
        }

        KyNangResponse kyNangResponse = new KyNangResponse();

        kyNangResponse.setMaKyNang( kyNang.getMaKyNang() );
        kyNangResponse.setTenKyNang( kyNang.getTenKyNang() );
        kyNangResponse.setMoTa( kyNang.getMoTa() );

        return kyNangResponse;
    }
}
