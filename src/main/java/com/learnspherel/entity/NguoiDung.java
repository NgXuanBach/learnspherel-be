package com.learnspherel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learnspherel.entity.enums.VaiTro;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "nguoi_dung")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NguoiDung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_nguoi_dung")
    private Long maNguoiDung;

    @Column(name = "ten_nguoi_dung")
    private String tenNguoiDung;

    @Column(name = "ten_dang_nhap", nullable = false, unique = true)
    private String tenDangNhap;

    @Column(name = "mat_khau", nullable = false)
    private String matKhau;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "vai_tro", nullable = false)
    private VaiTro vaiTro;

    @Column(name = "anh_dai_dien")
    private String anhDaiDien;

    @Column(name = "trang_thai", columnDefinition = "VARCHAR(20) DEFAULT 'HOAT_DONG'")
    @Enumerated(EnumType.STRING)
    private TrangThai trangThai;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @OneToMany(mappedBy = "nguoiDung")
    @JsonIgnore
    private List<DangKyKhoaHoc> dangKyKhoaHocs;

    @OneToMany(mappedBy = "nguoiDung")
    @JsonIgnore
    private List<GioHang> gioHangs;

    @OneToMany(mappedBy = "giangVien")
    @JsonIgnore
    private List<KhoaHoc> khoaHocGiangVien;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }

    public enum TrangThai {
        HOAT_DONG, XOA
    }
}