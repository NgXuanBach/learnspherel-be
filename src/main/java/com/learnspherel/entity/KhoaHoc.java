package com.learnspherel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learnspherel.entity.enums.ThoiHan;
import com.learnspherel.entity.enums.TrangThaiKhoaHoc;
import com.learnspherel.entity.enums.TrinhDo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "khoa_hoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhoaHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_khoa_hoc")
    private Long maKhoaHoc;

    @Column(name = "tieu_de", nullable = false)
    private String tieuDe;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "anh_dai_dien")
    private String anhDaiDien;

    @Enumerated(EnumType.STRING)
    @Column(name = "trinh_do", nullable = false)
    private TrinhDo trinhDo;

    @ManyToOne
    @JoinColumn(name = "ma_giang_vien", nullable = false)
    private NguoiDung giangVien;

    @Column(name = "co_phi", nullable = false)
    private Boolean coPhi;

    @Column(name = "gia")
    private Double gia;

    @Column(name = "video_demo_url")
    private String videoDemoUrl;

    @Column(name = "ngon_ngu")
    private String ngonNgu;

    @Enumerated(EnumType.STRING)
    @Column(name = "thoi_han")
    private ThoiHan thoiHan;

    @Column(name = "chung_chi")
    private Boolean chungChi;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private TrangThaiKhoaHoc trangThai;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @OneToMany(mappedBy = "khoaHoc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<BaiHoc> baiHocs;

    @OneToMany(mappedBy = "khoaHoc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<DangKyKhoaHoc> dangKyKhoaHocs;

    @OneToMany(mappedBy = "khoaHoc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<GioHang> gioHangs;

    @OneToMany(mappedBy = "khoaHoc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ChuongTrinh> chuongTrinhs;

    @ManyToMany
    @JoinTable(
            name = "khoa_hoc_ky_nang",
            joinColumns = @JoinColumn(name = "ma_khoa_hoc"),
            inverseJoinColumns = @JoinColumn(name = "ma_ky_nang")
    )
    @JsonIgnore
    private List<KyNang> kyNangs = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "KhoaHoc{" +
                "maKhoaHoc=" + maKhoaHoc +
                ", tieuDe='" + tieuDe + '\'' +
                ", trinhDo=" + trinhDo +
                ", coPhi=" + coPhi +
                ", gia=" + gia +
                ", anhDaiDien='" + anhDaiDien + '\'' +
                '}';
    }
}