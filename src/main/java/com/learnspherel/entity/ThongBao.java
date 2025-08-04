package com.learnspherel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learnspherel.entity.enums.LoaiThongBao;
import com.learnspherel.entity.enums.TrangThaiThongBao;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "thong_bao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThongBao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_thong_bao")
    private Long maThongBao;

    @ManyToOne
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    @JsonIgnore
    private NguoiDung nguoiDung;

    @Column(name = "tieu_de", nullable = false)
    private String tieuDe;

    @Column(name = "noi_dung", nullable = false, columnDefinition = "TEXT")
    private String noiDung;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai", nullable = false)
    private LoaiThongBao loai;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private TrangThaiThongBao trangThai;

    @Column(name = "ngay_gui")
    private LocalDateTime ngayGui;

    @PrePersist
    protected void onCreate() {
        ngayGui = LocalDateTime.now();
        trangThai = TrangThaiThongBao.CHUA_DOC;
    }

    @Override
    public String toString() {
        return "ThongBao{" +
                "maThongBao=" + maThongBao +
                ", tieuDe='" + tieuDe + '\'' +
                ", loai=" + loai +
                ", trangThai=" + trangThai +
                ", ngayGui=" + ngayGui +
                '}';
    }
}