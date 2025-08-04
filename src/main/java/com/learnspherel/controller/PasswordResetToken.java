package com.learnspherel.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_nguoi_dung", nullable = false)
    private Long maNguoiDung;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "thoi_gian_het_han", nullable = false)
    private LocalDateTime thoiGianHetHan;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private TrangThai trangThai;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    public enum TrangThai {
        HOAT_DONG, HET_HAN, DA_SUDUNG
    }

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
    }
}