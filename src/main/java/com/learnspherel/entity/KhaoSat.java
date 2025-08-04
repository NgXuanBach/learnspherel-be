package com.learnspherel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "khao_sat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhaoSat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_khao_sat")
    private Long maKhaoSat;

    @ManyToOne
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    @JsonIgnore
    private NguoiDung nguoiDung;

    @Column(name = "cau_tra_loi", nullable = false, columnDefinition = "JSON")
    private String cauTraLoi;

    @Column(name = "khoa_hoc_goi_y", columnDefinition = "JSON")
    private String khoaHocGoiY;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "KhaoSat{" +
                "maKhaoSat=" + maKhaoSat +
                ", cauTraLoi='" + cauTraLoi + '\'' +
                '}';
    }
}