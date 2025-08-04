package com.learnspherel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "danh_gia_giang_vien")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DanhGiaGiangVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_danh_gia")
    private Long maDanhGia;

    @ManyToOne
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    @JsonIgnore
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "ma_giang_vien", nullable = false)
    @JsonIgnore
    private NguoiDung giangVien;

    @Column(name = "diem_so", nullable = false)
    private Integer diemSo;

    @Column(name = "binh_luan", columnDefinition = "TEXT")
    private String binhLuan;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
    }
}