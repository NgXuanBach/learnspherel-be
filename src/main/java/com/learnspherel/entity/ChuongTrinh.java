package com.learnspherel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Entity ChuongTrinh
@Entity
@Table(name = "chuong_trinh")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChuongTrinh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_chuong_trinh")
    private Long maChuongTrinh;

    @ManyToOne
    @JoinColumn(name = "ma_khoa_hoc", nullable = false)
    @JsonIgnore
    private KhoaHoc khoaHoc;

    @Column(name = "tieu_de", nullable = false)
    private String tieuDe;

    @Column(name = "thu_tu_chuong", nullable = false)
    private Integer thuTuChuong;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @OneToMany(mappedBy = "chuongTrinh", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<BaiHoc> baiHocs = new ArrayList<>();

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