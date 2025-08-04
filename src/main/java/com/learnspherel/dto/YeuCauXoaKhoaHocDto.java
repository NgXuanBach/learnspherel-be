package com.learnspherel.dto;

import com.learnspherel.entity.enums.TrangThaiYeuCau;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class YeuCauXoaKhoaHocDto {
    private Long maYeuCau;
    private Long maKhoaHoc;
    private String tenKhoaHoc;
    private Long maGiangVien;
    private String tenGiangVien;
    private String lyDo;
    private String lyDoTuChoi;
    private TrangThaiYeuCau trangThai;
    private LocalDateTime ngayTao;
    // ... các trường khác nếu cần
}
