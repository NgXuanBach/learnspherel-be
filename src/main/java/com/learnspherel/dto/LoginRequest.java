package com.learnspherel.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
	@NotBlank(message = "{auth.login.username.empty}")
	private String nguoiDungHoacEmail;

	@NotBlank(message = "{auth.login.password.empty}")
	private String matKhau;
}
