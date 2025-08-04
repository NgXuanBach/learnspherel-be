package com.learnspherel.dto;

import com.learnspherel.entity.enums.ThoiHan;
import com.learnspherel.entity.enums.TrinhDo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KhoaHocDto {
    private Long maKhoaHoc;

    @NotBlank(message = "{khoahoc.tieuDe.empty}")
    @Size(max = 100, message = "{khoahoc.tieuDe.too.long}")
    private String tieuDe;

    @Size(max = 500, message = "{khoahoc.moTa.too.long}")
    private String moTa;

    @NotNull(message = "{khoahoc.trinhDo.empty}")
    private TrinhDo trinhDo;

    @NotNull(message = "{khoahoc.maGiangVien.empty}")
    @Positive(message = "{khoahoc.maGiangVien.invalid}")
    private Long maGiangVien;

    private Boolean coPhi;

    @Positive(message = "{khoahoc.gia.invalid}")
    private Double gia;
    private String anhDaiDien;
    private ThoiHan thoiHan;
    private String videoDemoUrl;
    private Boolean chungChi;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;
}