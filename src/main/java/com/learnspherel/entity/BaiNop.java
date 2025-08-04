package com.learnspherel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learnspherel.entity.enums.LoaiBaiNop;
import com.learnspherel.entity.enums.TrangThaiBaiNop;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bai_nop")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaiNop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_bai_nop")
    private Long maBaiNop;

    @ManyToOne
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    @JsonIgnore
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "ma_bai_kiem_tra", nullable = false)
    @JsonIgnore
    private BaiKiemTra baiKiemTra;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_bai_nop", nullable = false)
    private LoaiBaiNop loaiBaiNop;

    @Column(name = "noi_dung", nullable = false, columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "diem_so")
    private Integer diemSo;

    @Column(name = "phan_hoi", columnDefinition = "TEXT")
    private String phanHoi;

    @Column(name = "ngay_nop")
    private LocalDateTime ngayNop;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private TrangThaiBaiNop trangThai;

    @PrePersist
    protected void onCreate() {
        ngayNop = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "BaiNop{" +
                "maBaiNop=" + maBaiNop +
                ", diemSo=" + diemSo +
                ", trangThai=" + trangThai +
                '}';
    }
}