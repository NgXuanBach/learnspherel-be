package com.learnspherel.entity;

import com.learnspherel.entity.enums.CertificateStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chung_chi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maChungChi;

    @ManyToOne
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "ma_khoa_hoc", nullable = false)
    private KhoaHoc khoaHoc;

    private LocalDateTime ngayCap;
    private Integer diemCuoiKhoa;

    @Column(unique = true, length = 32)
    private String maChungChiXacNhan;

    @Enumerated(EnumType.STRING)
    private CertificateStatus trangThai;

    private String nguoiKy;
    private LocalDateTime ngayThuHoi;
    private String lyDoThuHoi;
    private LocalDateTime ngayCapNhat;
}