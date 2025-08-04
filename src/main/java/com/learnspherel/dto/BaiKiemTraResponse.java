package com.learnspherel.dto;

import com.learnspherel.entity.enums.LoaiBaiKiemTra;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaiKiemTraResponse {
    private Long maBaiKiemTra;
    private String tieuDe;
    private Integer diemDat;
    private LoaiBaiKiemTra loai;
}