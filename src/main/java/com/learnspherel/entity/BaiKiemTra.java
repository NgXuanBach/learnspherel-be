package com.learnspherel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learnspherel.entity.enums.LoaiBaiKiemTra;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bai_kiem_tra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaiKiemTra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_bai_kiem_tra")
    private Long maBaiKiemTra;

    @ManyToOne
    @JoinColumn(name = "ma_bai_hoc")
    @JsonIgnore
    private BaiHoc baiHoc;

    @ManyToOne
    @JoinColumn(name = "ma_khoa_hoc", nullable = false)
    @JsonIgnore
    private KhoaHoc khoaHoc;

    @Column(name = "tieu_de", nullable = false)
    private String tieuDe;

    @Column(name = "diem_dat", nullable = false)
    private Integer diemDat;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai", nullable = false)
    private LoaiBaiKiemTra loai;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @OneToMany(mappedBy = "baiKiemTra", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<CauHoi> cauHois;

    @OneToMany(mappedBy = "baiKiemTra", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TruongHopKiemThu> truongHopKiemThus;

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
        return "BaiKiemTra{" +
                "maBaiKiemTra=" + maBaiKiemTra +
                ", tieuDe='" + tieuDe + '\'' +
                ", diemDat=" + diemDat +
                ", loai=" + loai +
                '}';
    }
}