package com.learnspherel.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewTeacherRequestDTO {
    @NotNull
    private Long maNguoiDung;
    @NotNull
    private Long maGiangVien;
    @NotNull
    @Min(1) @Max(5)
    private Integer diemSo;
    private String binhLuan;
}
