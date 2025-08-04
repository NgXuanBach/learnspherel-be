package com.learnspherel.service;

import com.learnspherel.config.FileStorageProperties;
import com.learnspherel.dto.*;
import com.learnspherel.entity.NguoiDung;
import com.learnspherel.entity.PasswordResetToken;
import com.learnspherel.entity.enums.VaiTro;
import com.learnspherel.exception.PasswordResetTokenInvalidException;
import com.learnspherel.mapper.UserMapper;
import com.learnspherel.repository.NguoiDungRepository;
import com.learnspherel.repository.PasswordResetTokenRepository;
import com.learnspherel.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private NguoiDungRepository nguoiDungRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private CustomUserDetailsService userDetailsService;
    @Mock
    private MessageSource messageSource;
    @Mock
    private UserMapper userMapper;
    @Mock
    private FileStorageProperties fileStorageProperties;
    @Mock
    private JavaMailSender mailSender;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private MultipartFile avatar;

    @InjectMocks
    private AuthService authService;

    private NguoiDung nguoiDung;
    private UserResponse userResponse;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private UpdateUserRequest updateUserRequest;

    @BeforeEach
    void setUp() {
        // Thiết lập dữ liệu mẫu
        nguoiDung = new NguoiDung();
        nguoiDung.setMaNguoiDung(1L);
        nguoiDung.setTenDangNhap("testuser");
        nguoiDung.setEmail("test@example.com");
        nguoiDung.setMatKhau("encodedPassword");
        nguoiDung.setVaiTro(VaiTro.HOC_VIEN);
        nguoiDung.setAnhDaiDien("/img/avatar-default.jpg");
        nguoiDung.setTrangThai(NguoiDung.TrangThai.HOAT_DONG);

        userResponse = new UserResponse();
        userResponse.setMaNguoiDung(1L);
        userResponse.setTenDangNhap("testuser");
        userResponse.setEmail("test@example.com");

        registerRequest = new RegisterRequest();
        registerRequest.setTenDangNhap("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setMatKhau("Password123!");
        registerRequest.setTenNguoiDung("Test User");

        loginRequest = new LoginRequest();
        loginRequest.setNguoiDungHoacEmail("testuser");
        loginRequest.setMatKhau("Password123!");

        updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setTenNguoiDung("Updated User");
        updateUserRequest.setEmail("updated@example.com");
        updateUserRequest.setMatKhauCu("Password123!");
        updateUserRequest.setMatKhauMoi("NewPassword123!");

        // Thiết lập stubbing chung cho messageSource
        lenient().when(messageSource.getMessage(eq("auth.register.success"), any(), anyString(), any())).thenReturn("Đăng ký thành công");
        lenient().when(messageSource.getMessage(eq("auth.register.username.exists"), any(), anyString(), any())).thenReturn("Tên đăng nhập đã tồn tại");
        lenient().when(messageSource.getMessage(eq("auth.register.email.exists"), any(), anyString(), any())).thenReturn("Email đã được sử dụng");
        lenient().when(messageSource.getMessage(eq("auth.register.failure"), any(), anyString(), any())).thenReturn("Đăng ký thất bại");
        lenient().when(messageSource.getMessage(eq("auth.login.success"), any(), anyString(), any())).thenReturn("Đăng nhập thành công");
        lenient().when(messageSource.getMessage(eq("auth.get_users.success"), any(), anyString(), any())).thenReturn("Lấy thông tin thành công");
        lenient().when(messageSource.getMessage(eq("auth.login.username.notfound"), any(), anyString(), any())).thenReturn("Username or email not found");
        lenient().when(messageSource.getMessage(eq("auth.forgot.success"), any(), anyString(), any())).thenReturn("Password reset email sent successfully");
        lenient().when(messageSource.getMessage(eq("auth.forgot.email.failure"), any(), anyString(), any())).thenReturn("Failed to send reset email");
        lenient().when(messageSource.getMessage(eq("auth.unexpected.error"), any(), anyString(), any())).thenReturn("Unexpected error occurred");
        lenient().when(messageSource.getMessage(eq("auth.reset.success"), any(), anyString(), any())).thenReturn("Password reset successfully");
        lenient().when(messageSource.getMessage(eq("auth.reset.token.invalid"), any(), anyString(), any())).thenReturn("Invalid or expired reset token");
        lenient().when(messageSource.getMessage(eq("auth.reset.token.expired"), any(), anyString(), any())).thenReturn("Reset token has expired");
        lenient().when(messageSource.getMessage(eq("auth.update_role.success"), any(), anyString(), any())).thenReturn("Cập nhật vai trò thành công");
        lenient().when(messageSource.getMessage(eq("auth.unauthorized"), any(), anyString(), any())).thenReturn("Không có quyền thực hiện hành động này");
        lenient().when(messageSource.getMessage(eq("auth.user.notfound"), any(), anyString(), any())).thenReturn("Người dùng không tồn tại");
        lenient().when(messageSource.getMessage(eq("auth.delete_user.success"), any(), anyString(), any())).thenReturn("Xóa người dùng thành công");
        lenient().when(messageSource.getMessage(eq("auth.delete_user.failure"), any(), anyString(), any())).thenReturn("Xóa người dùng thất bại");
        lenient().when(messageSource.getMessage(eq("auth.user.deleted"), any(), anyString(), any())).thenReturn("Tài khoản đã bị xóa");
    }

    @Test
    void testRegister_Success_WithoutAvatar() {
        when(nguoiDungRepository.existsByTenDangNhap("testuser")).thenReturn(false);
        when(nguoiDungRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userMapper.toUser(registerRequest)).thenReturn(nguoiDung);
        when(passwordEncoder.encode(nguoiDung.getMatKhau())).thenReturn("encodedPassword");
        when(nguoiDungRepository.save(any(NguoiDung.class))).thenReturn(nguoiDung);

        ApiResponse<Void> response = authService.register(registerRequest, null);

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
        assertEquals("Đăng ký thành công", response.getMessage());
        verify(nguoiDungRepository).save(nguoiDung);
    }

    @Test
    void testRegister_UsernameExists() {
        when(nguoiDungRepository.existsByTenDangNhap("testuser")).thenReturn(true);

        ApiResponse<Void> response = authService.register(registerRequest, null);

        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.CONFLICT.value(), response.getStatusCode());
        assertEquals("Tên đăng nhập đã tồn tại", response.getMessage());
        verify(nguoiDungRepository, never()).save(any(NguoiDung.class));
    }

    @Test
    void testRegister_EmailExists() {
        when(nguoiDungRepository.existsByTenDangNhap("testuser")).thenReturn(false);
        when(nguoiDungRepository.existsByEmail("test@example.com")).thenReturn(true);

        ApiResponse<Void> response = authService.register(registerRequest, null);

        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.CONFLICT.value(), response.getStatusCode());
        assertEquals("Email đã được sử dụng", response.getMessage());
        verify(nguoiDungRepository, never()).save(any(NguoiDung.class));
    }

    @Test
    void testLogin_Success() {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwt-token");

        ApiResponse<TokenResponse> response = authService.login(loginRequest);

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Đăng nhập thành công", response.getMessage());
        assertEquals("jwt-token", response.getData().getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testLogin_BadCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testGetCurrentUser_Success() {
        when(jwtUtil.extractUsername("jwt-token")).thenReturn("testuser");
        when(nguoiDungRepository.findByUsernameOrEmail("testuser")).thenReturn(Optional.of(nguoiDung));
        when(userMapper.toDto(nguoiDung)).thenReturn(userResponse);

        ApiResponse<UserResponse> response = authService.getCurrentUser("jwt-token");

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Lấy thông tin thành công", response.getMessage());
        assertEquals(userResponse, response.getData());
    }


    @Test
    void testUpdateCurrentUser_EmailExists() {
        when(jwtUtil.extractUsername("jwt-token")).thenReturn("testuser");
        when(nguoiDungRepository.findByUsernameOrEmail("testuser")).thenReturn(Optional.of(nguoiDung));
        when(nguoiDungRepository.existsByEmail("updated@example.com")).thenReturn(true);

        ApiResponse<UserResponse> response = authService.updateCurrentUser(updateUserRequest, null, "jwt-token");

        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.CONFLICT.value(), response.getStatusCode());
        assertEquals("Email đã được sử dụng", response.getMessage());
        verify(nguoiDungRepository, never()).save(any(NguoiDung.class));
    }


    @Test
    void testForgotPassword_UserNotFound() {
        when(nguoiDungRepository.findByTenDangNhapOrEmail("testuser", "testuser")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.forgotPassword(new ForgotPasswordRequest("testuser")));
        assertTrue(exception.getCause() instanceof UsernameNotFoundException);
        assertEquals("Username or email not found", exception.getCause().getMessage());
    }

    @Test
    void testResetPassword_Success() {
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .maNguoiDung(1L)
                .token("reset-token")
                .thoiGianHetHan(LocalDateTime.now().plusMinutes(5))
                .trangThai(PasswordResetToken.TrangThai.HOAT_DONG)
                .build();
        when(passwordResetTokenRepository.findByTokenAndTrangThai("reset-token", PasswordResetToken.TrangThai.HOAT_DONG))
                .thenReturn(Optional.of(resetToken));
        when(nguoiDungRepository.findById(1L)).thenReturn(Optional.of(nguoiDung));
        when(passwordEncoder.encode("NewPassword123!")).thenReturn("newEncodedPassword");
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(resetToken);

        ApiResponse<Void> response = authService.resetPassword(new ResetPasswordRequest("reset-token", "NewPassword123!"));

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Password reset successfully", response.getMessage());
        verify(nguoiDungRepository).save(nguoiDung);
    }

    @Test
    void testResetPassword_TokenExpired() {
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .maNguoiDung(1L)
                .token("reset-token")
                .thoiGianHetHan(LocalDateTime.now().minusMinutes(5))
                .trangThai(PasswordResetToken.TrangThai.HOAT_DONG)
                .build();
        when(passwordResetTokenRepository.findByTokenAndTrangThai("reset-token", PasswordResetToken.TrangThai.HOAT_DONG))
                .thenReturn(Optional.of(resetToken));

        assertThrows(PasswordResetTokenInvalidException.class, () -> authService.resetPassword(new ResetPasswordRequest("reset-token", "NewPassword123!")));
        verify(passwordResetTokenRepository).save(resetToken);
    }

    @Test
    void testUpdateUserRole_Success() {
        NguoiDung admin = new NguoiDung();
        admin.setMaNguoiDung(2L);
        admin.setTenDangNhap("admin");
        admin.setVaiTro(VaiTro.QUAN_TRI);
        admin.setTrangThai(NguoiDung.TrangThai.HOAT_DONG);
        when(jwtUtil.extractUsername("jwt-token")).thenReturn("admin");
        when(nguoiDungRepository.findByTenDangNhapOrEmail("admin", "admin")).thenReturn(Optional.of(admin));
        when(nguoiDungRepository.findById(1L)).thenReturn(Optional.of(nguoiDung));
        when(userMapper.toDto(nguoiDung)).thenReturn(userResponse);
        when(nguoiDungRepository.save(any(NguoiDung.class))).thenReturn(nguoiDung);

        ApiResponse<UserResponse> response = authService.updateUserRole(1L, new UpdateRoleRequest(VaiTro.GIANG_VIEN), "jwt-token");

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Cập nhật vai trò thành công", response.getMessage());
        assertEquals(userResponse, response.getData());
        verify(nguoiDungRepository).save(nguoiDung);
    }

    @Test
    void testUpdateUserRole_NotAdmin() {
        NguoiDung nonAdmin = new NguoiDung();
        nonAdmin.setTenDangNhap("testuser");
        nonAdmin.setVaiTro(VaiTro.HOC_VIEN);
        nonAdmin.setTrangThai(NguoiDung.TrangThai.HOAT_DONG);
        when(jwtUtil.extractUsername("jwt-token")).thenReturn("testuser");
        when(nguoiDungRepository.findByTenDangNhapOrEmail("testuser", "testuser")).thenReturn(Optional.of(nonAdmin));

        ApiResponse<UserResponse> response = authService.updateUserRole(1L, new UpdateRoleRequest(VaiTro.GIANG_VIEN), "jwt-token");

        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatusCode());
        assertEquals("Không có quyền thực hiện hành động này", response.getMessage());
        verify(nguoiDungRepository, never()).save(any(NguoiDung.class));
    }

    @Test
    void testDeleteUser_Success() {
        NguoiDung admin = new NguoiDung();
        admin.setMaNguoiDung(2L);
        admin.setTenDangNhap("admin");
        admin.setVaiTro(VaiTro.QUAN_TRI);
        admin.setTrangThai(NguoiDung.TrangThai.HOAT_DONG);
        when(jwtUtil.extractUsername("jwt-token")).thenReturn("admin");
        when(nguoiDungRepository.findByTenDangNhapOrEmail("admin", "admin")).thenReturn(Optional.of(admin));
        when(nguoiDungRepository.findById(1L)).thenReturn(Optional.of(nguoiDung));
        when(nguoiDungRepository.save(any(NguoiDung.class))).thenReturn(nguoiDung);

        ApiResponse<Void> response = authService.deleteUser(1L, "jwt-token");

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Xóa người dùng thành công", response.getMessage());
        verify(nguoiDungRepository).save(nguoiDung);
    }

    @Test
    void testDeleteUser_NotAdmin() {
        NguoiDung nonAdmin = new NguoiDung();
        nonAdmin.setTenDangNhap("testuser");
        nonAdmin.setVaiTro(VaiTro.HOC_VIEN);
        nonAdmin.setTrangThai(NguoiDung.TrangThai.HOAT_DONG);
        when(jwtUtil.extractUsername("jwt-token")).thenReturn("testuser");
        when(nguoiDungRepository.findByTenDangNhapOrEmail("testuser", "testuser")).thenReturn(Optional.of(nonAdmin));

        ApiResponse<Void> response = authService.deleteUser(1L, "jwt-token");

        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatusCode());
        assertEquals("Không có quyền thực hiện hành động này", response.getMessage());
        verify(nguoiDungRepository, never()).save(any(NguoiDung.class));
    }
}