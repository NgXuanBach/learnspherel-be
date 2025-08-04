package com.learnspherel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KyNangResponse {
    private Long maKyNang;
    private String tenKyNang;
    private String moTa;
}