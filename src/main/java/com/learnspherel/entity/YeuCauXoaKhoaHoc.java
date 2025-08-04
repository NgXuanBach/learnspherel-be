package com.learnspherel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learnspherel.entity.enums.TrangThaiYeuCau;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "yeu_cau_xoa_khoa_hoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YeuCauXoaKhoaHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_yeu_cau")
    private Long maYeuCau;

    @ManyToOne
    @JoinColumn(name = "ma_khoa_hoc", nullable = false)
    @JsonIgnore
    private KhoaHoc khoaHoc;

    @ManyToOne
    @JoinColumn(name = "ma_giang_vien", nullable = false)
    @JsonIgnore
    private NguoiDung giangVien;

    @Column(name = "ly_do", columnDefinition = "TEXT")
    private String lyDo;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private TrangThaiYeuCau trangThai;
    
    @Column(name = "ly_do_tu_choi", columnDefinition = "TEXT")
    private String lyDoTuChoi;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

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
        return "YeuCauXoaKhoaHoc{" +
                "maYeuCau=" + maYeuCau +
                ", lyDo='" + lyDo + '\'' +
                ", trangThai=" + trangThai +
                '}';
    }
}