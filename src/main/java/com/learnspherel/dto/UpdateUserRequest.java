package com.learnspherel.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    @Size(max = 100, message = "{nguoidung.ten_nguoi_dung.too.long}")
    private String tenNguoiDung;

    @Email(message = "{auth.register.email.invalid}")
    @Size(max = 100, message = "{auth.register.email.too.long}")
    private String email;
    private String matKhauMoi; // Có thể null nếu không đổi mật khẩu
    private String matKhauCu;
} 