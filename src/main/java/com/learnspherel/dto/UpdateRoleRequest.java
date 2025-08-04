package com.learnspherel.dto;

import com.learnspherel.entity.enums.VaiTro;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateRoleRequest {
    @NotNull(message = "Vai trò không được để trống")
    private VaiTro vaiTro;
}
