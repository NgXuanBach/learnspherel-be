package com.learnspherel.dto;

import com.learnspherel.entity.enums.VaiTro;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long maNguoiDung;
    private String tenNguoiDung;
    private String tenDangNhap;
    private String email;
    private VaiTro vaiTro;
    private String anhDaiDien;
}
