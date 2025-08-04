package com.learnspherel.dto;

import com.learnspherel.entity.enums.PhuongThucThanhToan;
import com.learnspherel.entity.enums.TrangThaiGiaoDich;
import com.learnspherel.entity.enums.TrangThaiThanhToan;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DangKyKhoaHocResponse {
    @NotNull(message = "{dang_ky_khoa_hoc.maDangKy.empty}")
    @Positive(message = "{dang_ky_khoa_hoc.maDangKy.invalid}")
    private Long maDangKy;

    // Thông tin khóa học (chỉ các thuộc tính cần thiết)
    private Long maKhoaHoc;
    private String tieuDe;
    private String anhDaiDien;
    private Double gia;
    private Boolean coPhi;

    // Thông tin giao dịch
    private Long maGiaoDich;
    private Double soTien;
    private PhuongThucThanhToan phuongThuc;
    private String maGiaoDichNganHang;
    private TrangThaiGiaoDich trangThai;
    private LocalDateTime ngayGiaoDich;

    // Trạng thái đăng ký
    private TrangThaiThanhToan trangThaiThanhToan;
}