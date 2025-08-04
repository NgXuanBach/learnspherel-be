package com.learnspherel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learnspherel.entity.enums.TrangThaiPhien;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "phien_dang_nhap")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhienDangNhap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_phien")
    private Long maPhien;

    @ManyToOne
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    @JsonIgnore
    private NguoiDung nguoiDung;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "thoi_gian_het_han", nullable = false)
    private LocalDateTime thoiGianHetHan;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private TrangThaiPhien trangThai;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "PhienDangNhap{" +
                "maPhien=" + maPhien +
                ", nguoiDung=" + nguoiDung.getMaNguoiDung() +
                ", thoiGianHetHan=" + thoiGianHetHan +
                ", trangThai=" + trangThai +
                '}';
    }
}