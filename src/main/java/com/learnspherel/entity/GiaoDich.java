package com.learnspherel.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learnspherel.entity.enums.PhuongThucThanhToan;
import com.learnspherel.entity.enums.TrangThaiGiaoDich;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "giao_dich")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiaoDich {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_giao_dich")
    private Long maGiaoDich;

    @ManyToOne
    @JoinColumn(name = "ma_dang_ky", nullable = false)
    @JsonIgnore
    private DangKyKhoaHoc dangKy;

    @Column(name = "so_tien", nullable = false)
    private Double soTien;

    @Enumerated(EnumType.STRING)
    @Column(name = "phuong_thuc", nullable = false)
    private PhuongThucThanhToan phuongThuc;

    @Column(name = "ma_giao_dich_ngan_hang")
    private String maGiaoDichNganHang;

    @Column(name = "paypal_token")
    private String paypalToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private TrangThaiGiaoDich trangThai;

    @Column(name = "ngay_giao_dich")
    private LocalDateTime ngayGiaoDich;

    @PrePersist
    protected void onCreate() {
        ngayGiaoDich = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "GiaoDich{" +
                "maGiaoDich=" + maGiaoDich +
                ", soTien=" + soTien +
                ", phuongThuc=" + phuongThuc +
                ", maGiaoDichNganHang=" + maGiaoDichNganHang +
                ", paypalToken=" + paypalToken +
                ", trangThai=" + trangThai +
                '}';
    }
}