package com.learnspherel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learnspherel.entity.enums.TrangThaiThanhToan;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dang_ky_khoa_hoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DangKyKhoaHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_dang_ky")
    private Long maDangKy;

    @ManyToOne
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    @JsonIgnore
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "ma_khoa_hoc", nullable = false)
    @JsonIgnore
    private KhoaHoc khoaHoc;

    @Column(name = "ngay_dang_ky")
    private LocalDateTime ngayDangKy;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_thanh_toan", nullable = false)
    private TrangThaiThanhToan trangThaiThanhToan;

    @PrePersist
    protected void onCreate() {
        ngayDangKy = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "DangKyKhoaHoc{" +
                "maDangKy=" + maDangKy +
                ", nguoiDung=" + nguoiDung.getMaNguoiDung() +
                ", khoaHoc=" + khoaHoc.getMaKhoaHoc() +
                ", trangThaiThanhToan=" + trangThaiThanhToan +
                '}';
    }
}