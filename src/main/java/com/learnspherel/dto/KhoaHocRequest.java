package com.learnspherel.dto;

import com.learnspherel.entity.enums.ThoiHan;
import com.learnspherel.entity.enums.TrinhDo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhoaHocRequest {

    @NotBlank(message = "{khoa_hoc.tieu_de.not_blank}")
    private String tieuDe;

    private String moTa;

    private String anhDaiDien;

    @NotNull(message = "{khoa_hoc.trinh_do.not_null}")
    private TrinhDo trinhDo;

    @NotNull(message = "{khoa_hoc.ma_giang_vien.not_null}")
    @Positive(message = "{khoa_hoc.ma_giang_vien.positive}")
    private Long maGiangVien;

    @NotNull(message = "{khoa_hoc.co_phi.not_null}")
    private Boolean coPhi;

    @PositiveOrZero(message = "{khoa_hoc.gia.positive_or_zero}")
    private Double gia;

    private String videoDemoUrl;
    private String ngonNgu;

    private ThoiHan thoiHan;
    private String kyNang;
    private Boolean chungChi;
}