package com.learnspherel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "truong_hop_kiem_thu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TruongHopKiemThu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_truong_hop")
    private Long maTruongHop;

    @ManyToOne
    @JoinColumn(name = "ma_bai_kiem_tra", nullable = false)
    @JsonIgnore
    private BaiKiemTra baiKiemTra;

    @Column(name = "dau_vao", nullable = false, columnDefinition = "TEXT")
    private String dauVao;

    @Column(name = "dau_ra_mong_muon", nullable = false, columnDefinition = "TEXT")
    private String dauRaMongMuon;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "TruongHopKiemThu{" +
                "maTruongHop=" + maTruongHop +
                ", dauVao='" + dauVao + '\'' +
                ", dauRaMongMuon='" + dauRaMongMuon + '\'' +
                '}';
    }
}
