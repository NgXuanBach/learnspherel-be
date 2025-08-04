package com.learnspherel.mapper;

import com.learnspherel.dto.CertificateRequestDTO;
import com.learnspherel.dto.CertificateResponseDTO;
import com.learnspherel.entity.Certificate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CertificateMapper {
    @Mapping(source = "nguoiDung.tenNguoiDung", target = "tenNguoiDung")
    @Mapping(source = "khoaHoc.tieuDe", target = "tenKhoaHoc")
    @Mapping(source = "khoaHoc.giangVien.maNguoiDung", target = "maGiangVien")
    CertificateResponseDTO toResponseDto(Certificate entity);

    @Mapping(target = "maChungChi", ignore = true)
    @Mapping(target = "ngayCap", ignore = true)
    @Mapping(target = "ngayCapNhat", ignore = true)
    @Mapping(target = "trangThai", ignore = true)
    @Mapping(target = "maChungChiXacNhan", ignore = true)
    @Mapping(target = "nguoiDung", ignore = true)
    @Mapping(target = "khoaHoc", ignore = true)
    Certificate toEntity(CertificateRequestDTO dto);
}
