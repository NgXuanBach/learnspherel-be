package com.learnspherel.dto;

import com.learnspherel.entity.enums.LoaiBaiKiemTra;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaiKiemTraDto {

    private Long maBaiKiemTra;

    @NotNull(message = "{bai_kiem_tra.ma_khoa_hoc.not_null}")
    @Positive(message = "{bai_kiem_tra.ma_khoa_hoc.positive}")
    private Long maKhoaHoc;

    private Long maBaiHoc;

    @NotBlank(message = "{bai_kiem_tra.tieu_de.not_blank}")
    private String tieuDe;

    @NotNull(message = "{bai_kiem_tra.diem_dat.not_null}")
    @Positive(message = "{bai_kiem_tra.diem_dat.positive}")
    private Integer diemDat;

    @NotNull(message = "{bai_kiem_tra.loai.not_null}")
    private LoaiBaiKiemTra loai;

    private List<CauHoiDto> cauHois;
}