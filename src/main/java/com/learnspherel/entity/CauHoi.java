package com.learnspherel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cau_hoi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CauHoi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_cau_hoi")
    private Long maCauHoi;

    @ManyToOne
    @JoinColumn(name = "ma_bai_kiem_tra", nullable = false)
    @JsonIgnore
    private BaiKiemTra baiKiemTra;

    @Column(name = "noi_dung", nullable = false, columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "dap_an", columnDefinition = "JSON")
    private String dapAn;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "CauHoi{" +
                "maCauHoi=" + maCauHoi +
                ", noiDung='" + noiDung + '\'' +
                '}';
    }
}