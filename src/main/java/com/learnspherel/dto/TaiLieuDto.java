package com.learnspherel.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaiLieuDto {
    private Long maTaiLieu;

    @NotNull(message = "{tailieu.maBaiHoc.empty}")
    @Positive(message = "{tailieu.maBaiHoc.invalid}")
    private Long maBaiHoc;

    private Long maBaiNop = null;

//    @NotBlank(message = "{tailieu.tenFile.empty}")
//    @Size(max = 255, message = "{tailieu.tenFile.too.long}")
    private String tenFile;

//    @NotBlank(message = "{tailieu.duongDan.empty}")
//    @Size(max = 255, message = "{tailieu.duongDan.too.long}")
    private String duongDan;

//    @NotNull(message = "{tailieu.kichThuoc.empty}")
//    @Positive(message = "{tailieu.kichThuoc.invalid}")
    private Integer kichThuoc;

    @NotNull(message = "{tailieu.loaiFile.empty}")
    private String loaiFile;

    private LocalDateTime ngayTaiLen;
}