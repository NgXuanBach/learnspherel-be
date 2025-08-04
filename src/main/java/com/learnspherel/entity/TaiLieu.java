package com.learnspherel.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learnspherel.entity.enums.LoaiTaiLieu;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tai_lieu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaiLieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_tai_lieu")
    private Long maTaiLieu;

    @ManyToOne
    @JoinColumn(name = "ma_bai_hoc")
    @JsonIgnore
    private BaiHoc baiHoc;

    @ManyToOne
    @JoinColumn(name = "ma_bai_nop")
    @JsonIgnore
    private BaiNop baiNop;

    @Column(name = "ten_file", nullable = false)
    private String tenFile;

    @Column(name = "duong_dan", nullable = false)
    private String duongDan;

    @Column(name = "kich_thuoc")
    private Integer kichThuoc;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_file", nullable = false)
    private LoaiTaiLieu loaiFile;

    @Column(name = "ngay_tai_len")
    private LocalDateTime ngayTaiLen;

    @PrePersist
    protected void onCreate() {
        ngayTaiLen = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "TaiLieu{" +
                "maTaiLieu=" + maTaiLieu +
                ", tenFile='" + tenFile + '\'' +
                ", loaiFile=" + loaiFile +
                '}';
    }
}