package com.learnspherel.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "{auth.register.username.empty}")
    @Pattern(regexp = "^\\S+$", message = "{auth.register.username.no.spaces}")
    @Size(max = 50, message = "{auth.register.username.too.long}")
    private String tenDangNhap;

    @NotBlank(message = "{auth.register.user.name.empty}")
//    @Pattern(regexp = "^\\S+$", message = "{auth.register.user.name.no.spaces}")
    @Size(max = 100, message = "{auth.register.user.name.too.long}")
    private String tenNguoiDung;

    @NotBlank(message = "{auth.register.email.empty}")
    @Email(message = "{auth.register.email.invalid}")
    private String email;

    @NotBlank(message = "{auth.register.password.empty}")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&#^]).{8,}$",
            message = "{auth.register.password.invalid}"
    )
    @Size(max = 60, message = "{auth.register.password.too.long}")
    private String matKhau;
}