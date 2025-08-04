package com.learnspherel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learnspherel.entity.enums.TrangThaiHocTap;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tien_do_hoc_tap")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TienDoHocTap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_tien_do")
    private Long maTienDo;

    @ManyToOne
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    @JsonIgnore
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "ma_khoa_hoc", nullable = false)
    @JsonIgnore
    private KhoaHoc khoaHoc;

    @ManyToOne
    @JoinColumn(name = "ma_bai_hoc")
    @JsonIgnore
    private BaiHoc baiHoc;

    @ManyToOne
    @JoinColumn(name = "ma_bai_kiem_tra")
    @JsonIgnore
    private BaiKiemTra baiKiemTra;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private TrangThaiHocTap trangThai;

    @Column(name = "diem_so")
    private Integer diemSo;

    @Column(name = "ngay_hoan_thanh")
    private LocalDateTime ngayHoanThanh;

    @Override
    public String toString() {
        return "TienDoHocTap{" +
                "maTienDo=" + maTienDo +
                ", diemSo=" + diemSo +
                ", trangThai=" + trangThai +
                '}';
    }
}