package com.learnspherel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "khoa_hoc_ky_nang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhoaHocKyNang {

    @Id
    @ManyToOne
    @JoinColumn(name = "ma_khoa_hoc", nullable = false)
    @JsonIgnore
    private KhoaHoc khoaHoc;

    @Id
    @ManyToOne
    @JoinColumn(name = "ma_ky_nang", nullable = false)
    @JsonIgnore
    private KyNang kyNang;
}