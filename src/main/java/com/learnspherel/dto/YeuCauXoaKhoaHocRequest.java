package com.learnspherel.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YeuCauXoaKhoaHocRequest {
    private Long maKhoaHoc;
    private Long maGiangVien;
    private String lyDo;
}
