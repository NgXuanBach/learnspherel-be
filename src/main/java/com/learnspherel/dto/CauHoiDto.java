package com.learnspherel.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CauHoiDto {

    private Long maCauHoi;

    @NotBlank(message = "{cau_hoi.noi_dung.not_blank}")
    private String noiDung;

    private String dapAn;
}