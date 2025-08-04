package com.learnspherel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaiHocResponse {
    private Long maBaiHoc;
    private String tieuDe;
    private String videoUrl;
    private String thoiLuong;
    private Integer thuTuBai;
    private String status; // "watched", "unwatched", "watching"
}