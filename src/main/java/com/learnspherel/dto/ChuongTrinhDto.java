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
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChuongTrinhDto {
    private Long maChuongTrinh;

    @NotNull(message = "{chuongtrinh.maKhoaHoc.empty}")
    @Positive(message = "{chuongtrinh.maKhoaHoc.invalid}")
    private Long maKhoaHoc;

    @NotBlank(message = "{chuongtrinh.tieuDe.empty}")
    @Size(max = 255, message = "{chuongtrinh.tieuDe.too.long}")
    private String tieuDe;

    @NotNull(message = "{chuongtrinh.thuTuChuong.empty}")
    @Positive(message = "{chuongtrinh.thuTuChuong.invalid}")
    private Integer thuTuChuong;

    @Size(max = 5000, message = "{chuongtrinh.moTa.too.long}")
    private String moTa;

    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;

    private List<BaiHocDto> baiHocs;
}