package com.learnspherel.mapper;

import com.learnspherel.dto.TaiLieuDto;
import com.learnspherel.entity.BaiHoc;
import com.learnspherel.entity.BaiNop;
import com.learnspherel.entity.TaiLieu;
import com.learnspherel.entity.enums.LoaiTaiLieu;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TaiLieuMapper {

    @Mappings({
            @Mapping(source = "baiHoc.maBaiHoc", target = "maBaiHoc"),
            @Mapping(source = "baiNop.maBaiNop", target = "maBaiNop"),
            @Mapping(source = "loaiFile", target = "loaiFile", qualifiedByName = "enumToString")
    })
    TaiLieuDto taiLieuToTaiLieuDto(TaiLieu taiLieu);

    @Mappings({
            @Mapping(source = "maBaiHoc", target = "baiHoc", qualifiedByName = "mapBaiHoc"),
            @Mapping(source = "maBaiNop", target = "baiNop", qualifiedByName = "mapBaiNop"),
            @Mapping(source = "loaiFile", target = "loaiFile", qualifiedByName = "stringToEnum")
    })
    TaiLieu taiLieuDtoToTaiLieu(TaiLieuDto dto);

    // --- Custom Mappings ---

    @Named("enumToString")
    static String enumToString(LoaiTaiLieu loai) {
        return loai != null ? loai.name() : null;
    }

    @Named("stringToEnum")
    static LoaiTaiLieu stringToEnum(String loai) {
        return loai != null ? LoaiTaiLieu.valueOf(loai) : null;
    }

    @Named("mapBaiHoc")
    static BaiHoc mapBaiHoc(Long maBaiHoc) {
        return maBaiHoc != null ? BaiHoc.builder().maBaiHoc(maBaiHoc).build() : null;
    }

    @Named("mapBaiNop")
    static BaiNop mapBaiNop(Long maBaiNop) {
        return maBaiNop != null ? BaiNop.builder().maBaiNop(maBaiNop).build() : null;
    }
}
