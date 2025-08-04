package com.learnspherel.dto;

import com.learnspherel.entity.enums.CertificateStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CertificateResponseDTO {
    private Long maChungChi;
    private String maChungChiXacNhan;
    private String tenNguoiDung;
    private String tenKhoaHoc;
    private String nguoiKy;
    private Integer diemCuoiKhoa;
    private Long maGiangVien;
    private LocalDateTime ngayCap;
    private CertificateStatus trangThai;
}
