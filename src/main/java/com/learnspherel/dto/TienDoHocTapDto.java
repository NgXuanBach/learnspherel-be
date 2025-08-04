package com.learnspherel.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TienDoHocTapDto {
    @NotNull(message = "{tiendo.maBaiHoc.empty}")
    @Positive(message = "{tiendo.maBaiHoc.invalid}")
    private Long maBaiHoc;

    @NotNull(message = "{tiendo.trangThai.empty}")
    private String trangThai; // "HOAN_THANH" hoặc "DANG_HOC"
}