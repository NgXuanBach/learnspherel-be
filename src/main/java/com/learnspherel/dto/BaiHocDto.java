package com.learnspherel.dto;

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
public class BaiHocDto {
    private Long maBaiHoc;

    @NotNull(message = "{baihoc.maKhoaHoc.empty}")
    @Positive(message = "{baihoc.maKhoaHoc.invalid}")
    private Long maKhoaHoc;

    @NotBlank(message = "{baihoc.tieuDe.empty}")
    @Size(max = 255, message = "{baihoc.tieuDe.too.long}")
    private String tieuDe;

    @NotNull(message = "{baihoc.thuTuBaiHoc.empty}")
    @Positive(message = "{baihoc.thuTuBaiHoc.invalid}")
    private Integer thuTuBaiHoc;

    private String videoUrl;

    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;
}