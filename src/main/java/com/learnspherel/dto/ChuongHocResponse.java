package com.learnspherel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChuongHocResponse {
    private Long maChuong;
    private String tenChuong;
    private Integer thuTuChuong;
    private List<BaiHocResponse> baiHoc;
}