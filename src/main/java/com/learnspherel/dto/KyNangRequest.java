package com.learnspherel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KyNangRequest {

    @NotBlank(message = "{kynang.tenKyNang.empty}")
    @Size(max = 100, message = "{kynang.tenKyNang.too.long}")
    private String tenKyNang;

    @Size(max = 500, message = "{kynang.moTa.too.long}")
    private String moTa;
}