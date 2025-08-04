package com.learnspherel.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class CertificateRequestDTO {
    @NotNull
    private Long maNguoiDung;
    @NotNull
    private Long maKhoaHoc;
    private Integer diemCuoiKhoa;
    private String nguoiKy;
}
