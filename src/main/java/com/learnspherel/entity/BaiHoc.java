package com.learnspherel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bai_hoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaiHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_bai_hoc")
    private Long maBaiHoc;

    @ManyToOne
    @JoinColumn(name = "ma_khoa_hoc", nullable = false)
    @JsonIgnore
    private KhoaHoc khoaHoc;

    @ManyToOne
    @JoinColumn(name = "ma_chuong_trinh")
    @JsonIgnore
    private ChuongTrinh chuongTrinh;

    @Column(name = "tieu_de", nullable = false)
    private String tieuDe;

    @Column(name = "thu_tu_bai_hoc", nullable = false)
    private Integer thuTuBaiHoc;

    @Column(name = "noi_dung", columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "thoi_luong")
    private Double thoiLuong;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @OneToMany(mappedBy = "baiHoc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<TaiLieu> taiLieus;

    @OneToMany(mappedBy = "baiHoc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<BaiKiemTra> baiKiemTras;

    @OneToMany(mappedBy = "baiHoc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<TienDoHocTap> tienDoHocTaps;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
}