package com.learnspherel.mapper;

import com.learnspherel.dto.KyNangRequest;
import com.learnspherel.dto.KyNangResponse;
import com.learnspherel.entity.KyNang;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface KyNangMapper {

    KyNang toEntity(KyNangRequest request);

    @Mapping(target = "maKyNang", source = "maKyNang")
    @Mapping(target = "tenKyNang", source = "tenKyNang")
    @Mapping(target = "moTa", source = "moTa")
    KyNangResponse toResponse(KyNang kyNang);
}