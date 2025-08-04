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
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthService {

    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final MessageSource messageSource;
    private final UserMapper userMapper;
    private final FileStorageProperties fileStorageProperties;
    private final JavaMailSender mailSender;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${learnspherel.upload.dir}")
    private String uploadDirBase;
    @Value("${spring.mail.sender}")
    private String mailSenderAddress;

    @Value("${learnspherel.frontend.url}")
    private String frontendUrl;
    // Regex để kiểm tra mật khẩu: ít nhất 1 chữ hoa, 1 chữ thường, 1 số, 1 ký tự đặc biệt, tối thiểu 8 ký tự
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    public AuthService(NguoiDungRepository nguoiDungRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtUtil jwtUtil, CustomUserDetailsService userDetailsService,
                       MessageSource messageSource, UserMapper userMapper, FileStorageProperties fileStorageProperties,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       JavaMailSender mailSender) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.messageSource = messageSource;
        this.userMapper = userMapper;
        this.fileStorageProperties = fileStorageProperties;
        this.mailSender = mailSender;
        this.passwordResetTokenRepository = passwordResetTokenRepository;

    }

    /**
     * Đăng ký người dùng mới với tùy chọn upload avatar
     *
     * @param registerRequest Thông tin người dùng (tenDangNhap, email, matKhau, tenNguoiDung)
     * @param avatar          File avatar (tùy chọn)
     * @return ApiResponse Thông tin phản hồi
     */
    public ApiResponse<Void> register(RegisterRequest registerRequest, MultipartFile avatar) {
        if (nguoiDungRepository.existsByTenDangNhap(registerRequest.getTenDangNhap())) {
            return buildResponse(false, HttpStatus.CONFLICT,
                    messageSource.getMessage("auth.register.username.exists", null, "Tên đăng nhập đã tồn tại", null), null);
        }
        if (nguoiDungRepository.existsByEmail(registerRequest.getEmail())) {
            return buildResponse(false, HttpStatus.CONFLICT,
                    messageSource.getMessage("auth.register.email.exists", null, "Email đã được sử dụng", null), null);
        }

        try {
            NguoiDung nguoiDung = userMapper.toUser(registerRequest);
            nguoiDung.setMatKhau(passwordEncoder.encode(nguoiDung.getMatKhau()));
            nguoiDung.setVaiTro(VaiTro.HOC_VIEN);

            // Xử lý upload avatar nếu có
            if (avatar != null && !avatar.isEmpty()) {
                long maxSizeInBytes = convertToBytes(fileStorageProperties.getMaxFileSize());
                if (avatar.getSize() > maxSizeInBytes) {
                    return buildResponse(false, HttpStatus.BAD_REQUEST,
                            messageSource.getMessage("auth.register.avatar.too.large",
                                    new Object[]{fileStorageProperties.getMaxFileSize()},
                                    "File ảnh vượt quá kích thước tối đa", null), null);
                }

                String contentType = avatar.getContentType();
                if (!isValidImageType(contentType)) {
                    return buildResponse(false, HttpStatus.BAD_REQUEST,
                            messageSource.getMessage("auth.register.avatar.invalid", null,
                                    "Định dạng file ảnh không hợp lệ", null), null);
                }

                String originalFilename = avatar.getOriginalFilename();
                if (originalFilename == null || originalFilename.isEmpty()) {
                    throw new IllegalArgumentException("Tên file không hợp lệ");
                }

                String uploadDir = uploadDirBase + "uploads/image/avatars/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = UUID.randomUUID() + "_" + originalFilename;
                Path filePath = uploadPath.resolve(fileName);
                Files.write(filePath, avatar.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                nguoiDung.setAnhDaiDien("/uploads/image/avatars/" + fileName);
            }

            nguoiDungRepository.save(nguoiDung);
            return buildResponse(true, HttpStatus.CREATED,
                    messageSource.getMessage("auth.register.success", null, "Đăng ký thành công", null), null);
        } catch (Exception e) {
            throw new RuntimeException(
                    messageSource.getMessage("auth.register.failure", null, "Đăng ký thất bại", null), e);
        }
    }

    /**
     * Đăng nhập người dùng
     */
    public ApiResponse<TokenResponse> login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getNguoiDungHoacEmail(), request.getMatKhau()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getNguoiDungHoacEmail());
            String accessToken = jwtUtil.generateToken(userDetails);
            TokenResponse tokenResponse = new TokenResponse(accessToken, "Bearer");
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("auth.login.success", null, "Đăng nhập thành công", null), tokenResponse);
        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Lấy thông tin user hiện tại từ token
     */
    public ApiResponse<UserResponse> getCurrentUser(String token) {
//        if (token == null || token.isEmpty()) {
//            return buildResponse(false, HttpStatus.UNAUTHORIZED,
//                    messageSource.getMessage("auth.authenticate.invalid", null, "Xác thực không thành công", null), null);
//        }
        try {
            String sub = jwtUtil.extractUsername(token);
            NguoiDung user = nguoiDungRepository
                    .findByUsernameOrEmail(sub)
                    .orElseThrow(() -> new UsernameNotFoundException("User không tồn tại"));
            UserResponse userDto = userMapper.toDto(user);
            return buildResponse(true, HttpStatus.OK, "Lấy thông tin thành công", userDto);
        } catch (Exception e) {
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lấy thông tin người dùng thất bại", null);
        }
    }

    /**
     * Cập nhật thông tin user hiện tại từ token
     */
    public ApiResponse<UserResponse> updateCurrentUser(UpdateUserRequest request, MultipartFile file, String token) {
//        if (token == null || token.isEmpty()) {
//            return buildResponse(false, HttpStatus.UNAUTHORIZED, "Token không hợp lệ", null);
//        }
        try {
            String sub = jwtUtil.extractUsername(token);
            NguoiDung user = nguoiDungRepository
                    .findByUsernameOrEmail(sub)
                    .orElseThrow(() -> new UsernameNotFoundException("User không tồn tại"));

            if (request.getTenNguoiDung() != null && !request.getTenNguoiDung().isEmpty()) {
                user.setTenNguoiDung(request.getTenNguoiDung());
            }
            if (request.getEmail() != null && !request.getEmail().isEmpty()) {
                if (nguoiDungRepository.existsByEmail(request.getEmail()) &&
                        !request.getEmail().equals(user.getEmail())) {
                    return buildResponse(false, HttpStatus.CONFLICT,
                            messageSource.getMessage("auth.register.email.exists", null, "Email đã được sử dụng", null), null);
                }
                user.setEmail(request.getEmail());
            }
            if (request.getMatKhauMoi() != null && !request.getMatKhauMoi().isEmpty()) {
                if (request.getMatKhauCu() == null || request.getMatKhauCu().isEmpty()) {
                    return buildResponse(false, HttpStatus.BAD_REQUEST,
                            messageSource.getMessage("auth.update.old.password.empty", null, "Vui lòng nhập mật khẩu cũ", null), null);
                }
                // Verify old password
                if (!passwordEncoder.matches(request.getMatKhauCu(), user.getMatKhau())) {
                    return buildResponse(false, HttpStatus.UNAUTHORIZED,
                            messageSource.getMessage("auth.update.old.password.incorrect", null, "Mật khẩu cũ không đúng", null), null);
                }
                // Validate new password
                if (!PASSWORD_PATTERN.matcher(request.getMatKhauMoi()).matches()) {
                    return buildResponse(false, HttpStatus.BAD_REQUEST,
                            messageSource.getMessage("auth.update.new.password.invalid", null,
                                    "Mật khẩu mới phải chứa ít nhất 1 chữ hoa, 1 chữ thường, 1 số, 1 ký tự đặc biệt và tối thiểu 8 ký tự", null), null);
                }
                user.setMatKhau(passwordEncoder.encode(request.getMatKhauMoi()));
            }

            if (file != null && !file.isEmpty()) {
                long maxSizeInBytes = convertToBytes(fileStorageProperties.getMaxFileSize());
                if (file.getSize() > maxSizeInBytes) {
                    return buildResponse(false, HttpStatus.BAD_REQUEST,
                            messageSource.getMessage("nguoidung.upload_anh.file.too.large",
                                    new Object[]{fileStorageProperties.getMaxFileSize()},
                                    "File ảnh vượt quá kích thước tối đa", null), null);
                }

                String contentType = file.getContentType();
                if (!isValidImageType(contentType)) {
                    return buildResponse(false, HttpStatus.BAD_REQUEST,
                            messageSource.getMessage("nguoidung.upload_anh.file.invalid", null,
                                    "Định dạng file ảnh không hợp lệ", null), null);
                }

                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null || originalFilename.isEmpty()) {
                    throw new IllegalArgumentException("Tên file không hợp lệ");
                }
                String uploadDir = uploadDirBase + "uploads/image/avatars/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = UUID.randomUUID() + "_" + originalFilename;
                Path filePath = uploadPath.resolve(fileName);
                Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                if (user.getAnhDaiDien() != null && !user.getAnhDaiDien().isEmpty() && !user.getAnhDaiDien().equalsIgnoreCase("/img/avatar-default.jpg")) {
                    String oldFilePath = uploadDirBase + user.getAnhDaiDien().substring(1);
                    Files.deleteIfExists(Paths.get(oldFilePath));
                }

                user.setAnhDaiDien("/uploads/image/avatars/" + fileName);
            } else if (user.getAnhDaiDien().isEmpty()) {
                user.setAnhDaiDien("/img/avatar-default.jpg");
            } else if (file != null && file.getOriginalFilename().equalsIgnoreCase("blob")) {
                user.setAnhDaiDien("/img/avatar-default.jpg");
            }
            nguoiDungRepository.save(user);
            UserResponse userDto = userMapper.toDto(user);
            return buildResponse(true, HttpStatus.OK, "Cập nhật thông tin thành công", userDto);
        }
        catch (Exception e) {
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Cập nhật thông tin người dùng thất bại", null);
        }
    }

    @Transactional
    public ApiResponse<Void> forgotPassword(ForgotPasswordRequest request) {
        try {
            NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhapOrEmail(request.getEmailOrUsername(), request.getEmailOrUsername())
                    .orElseThrow(() -> new UsernameNotFoundException(
                            messageSource.getMessage("auth.login.username.notfound", null, "Username or email not found", null)));

            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .maNguoiDung(nguoiDung.getMaNguoiDung())
                    .token(token)
                    .thoiGianHetHan(LocalDateTime.now().plusMinutes(15))
                    .trangThai(PasswordResetToken.TrangThai.HOAT_DONG)
                    .build();
            passwordResetTokenRepository.save(resetToken);

            sendResetPasswordEmail(nguoiDung.getEmail(), token);

            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("auth.forgot.success", null, "Password reset email sent successfully", null), null);
        } catch (MessagingException e) {
            throw new RuntimeException(
                    messageSource.getMessage("auth.forgot.email.failure", null, "Failed to send reset email", null), e);
        } catch (Exception e) {
            throw new RuntimeException(
                    messageSource.getMessage("auth.unexpected.error", null, "Unexpected error occurred", null), e);
        }
    }

    @Transactional
    public ApiResponse<Void> resetPassword(ResetPasswordRequest request) {
        try {
            PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenAndTrangThai(request.getToken(), PasswordResetToken.TrangThai.HOAT_DONG)
                    .orElseThrow(() -> new PasswordResetTokenInvalidException(
                            messageSource.getMessage("auth.reset.token.invalid", null, "Invalid or expired reset token", null)));

            if (resetToken.getThoiGianHetHan().isBefore(LocalDateTime.now())) {
                resetToken.setTrangThai(PasswordResetToken.TrangThai.HET_HAN);
                passwordResetTokenRepository.save(resetToken);
                throw new PasswordResetTokenInvalidException(
                        messageSource.getMessage("auth.reset.token.expired", null, "Reset token has expired", null));
            }

            NguoiDung nguoiDung = nguoiDungRepository.findById(resetToken.getMaNguoiDung())
                    .orElseThrow(() -> new UsernameNotFoundException(
                            messageSource.getMessage("auth.login.username.notfound", null, "User not found", null)));

            nguoiDung.setMatKhau(passwordEncoder.encode(request.getNewPassword()));
            nguoiDungRepository.save(nguoiDung);

            resetToken.setTrangThai(PasswordResetToken.TrangThai.DA_SUDUNG);
            passwordResetTokenRepository.save(resetToken);

            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("auth.reset.success", null, "Password reset successfully", null), null);
        } catch (PasswordResetTokenInvalidException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    messageSource.getMessage("auth.unexpected.error", null, "Unexpected error occurred", null), e);
        }
    }

    public ApiResponse<UserResponse> updateUserRole(Long userId, UpdateRoleRequest request, String token) {
        try {
            // Kiểm tra quyền admin
            String sub = jwtUtil.extractUsername(token);
            NguoiDung currentUser = nguoiDungRepository
                    .findByTenDangNhapOrEmail(sub, sub)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            messageSource.getMessage("auth.login.username.notfound", null, "Người dùng không tồn tại", null)));
            if (!currentUser.getVaiTro().equals(VaiTro.QUAN_TRI)) {
                return buildResponse(false, HttpStatus.FORBIDDEN,
                        messageSource.getMessage("auth.unauthorized", null, "Không có quyền thực hiện hành động này", null), null);
            }

            // Kiểm tra người dùng tồn tại
            NguoiDung user = nguoiDungRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            messageSource.getMessage("auth.user.notfound", null, "Người dùng không tồn tại", null)));

            // Cập nhật vai trò
            if (request.getVaiTro() == null) {
                return buildResponse(false, HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("auth.role.invalid", null, "Vai trò không hợp lệ", null), null);
            }
            user.setVaiTro(request.getVaiTro());
            nguoiDungRepository.save(user);

            UserResponse userDto = userMapper.toDto(user);
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("auth.update_role.success", null, "Cập nhật vai trò thành công", null), userDto);
        } catch (UsernameNotFoundException e) {
            return buildResponse(false, HttpStatus.NOT_FOUND,
                    e.getMessage(), null);
        } catch (Exception e) {
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("auth.update_role.failure", null, "Cập nhật vai trò thất bại", null), null);
        }
    }

    public ApiResponse<List<UserResponse>> getAllUsers(String token) {
        try {
            // Kiểm tra quyền admin
            String sub = jwtUtil.extractUsername(token);
            NguoiDung currentUser = nguoiDungRepository
                    .findByTenDangNhapOrEmail(sub, sub)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            messageSource.getMessage("auth.login.username.notfound", null, "Người dùng không tồn tại", null)));
            if (currentUser.getTrangThai() != NguoiDung.TrangThai.HOAT_DONG) {
                return buildResponse(false, HttpStatus.FORBIDDEN,
                        messageSource.getMessage("auth.user.deleted", null, "Tài khoản đã bị xóa", null), null);
            }
            if (!currentUser.getVaiTro().equals(VaiTro.QUAN_TRI)) {
                return buildResponse(false, HttpStatus.FORBIDDEN,
                        messageSource.getMessage("auth.unauthorized", null, "Không có quyền thực hiện hành động này", null), null);
            }

            // Lấy danh sách người dùng đang hoạt động
            List<NguoiDung> users = nguoiDungRepository.findAllActive();
            List<UserResponse> userResponses = users.stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());

            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("auth.get_users.success", null, "Lấy danh sách người dùng thành công", null), userResponses);
        } catch (UsernameNotFoundException e) {
            return buildResponse(false, HttpStatus.NOT_FOUND,
                    e.getMessage(), null);
        } catch (Exception e) {
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("auth.get_users.failure", null, "Lấy danh sách người dùng thất bại", null), null);
        }
    }

    public ApiResponse<Void> deleteUser(Long userId, String token) {
        try {
            // Kiểm tra quyền admin
            String sub = jwtUtil.extractUsername(token);
            NguoiDung currentUser = nguoiDungRepository
                    .findByTenDangNhapOrEmail(sub, sub)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            messageSource.getMessage("auth.login.username.notfound", null, "Người dùng không tồn tại", null)));
            if (currentUser.getTrangThai() != NguoiDung.TrangThai.HOAT_DONG) {
                return buildResponse(false, HttpStatus.FORBIDDEN,
                        messageSource.getMessage("auth.user.deleted", null, "Tài khoản đã bị xóa", null), null);
            }
            if (!currentUser.getVaiTro().equals(VaiTro.QUAN_TRI)) {
                return buildResponse(false, HttpStatus.FORBIDDEN,
                        messageSource.getMessage("auth.unauthorized", null, "Không có quyền thực hiện hành động này", null), null);
            }

            // Kiểm tra người dùng tồn tại
            NguoiDung user = nguoiDungRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            messageSource.getMessage("auth.user.notfound", null, "Người dùng không tồn tại", null)));
            if (user.getTrangThai() != NguoiDung.TrangThai.HOAT_DONG) {
                return buildResponse(false, HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("auth.user.deleted", null, "Tài khoản đã bị xóa", null), null);
            }

            // Xóa mềm: Đặt trạng thái thành XOA
            user.setTrangThai(NguoiDung.TrangThai.XOA);
            nguoiDungRepository.save(user);

            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("auth.delete_user.success", null, "Xóa người dùng thành công", null), null);
        } catch (UsernameNotFoundException e) {
            return buildResponse(false, HttpStatus.NOT_FOUND,
                    e.getMessage(), null);
        } catch (Exception e) {
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("auth.delete_user.failure", null, "Xóa người dùng thất bại", null), null);
        }
    }

    private void sendResetPasswordEmail(String email, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("LearnSpherel");
        helper.setTo(email);
        helper.setSubject(messageSource.getMessage("auth.email.reset.subject", null, "Đặt lại mật khẩu - LearnSpherel", null));

        String resetUrl = String.format("%s/reset-password.html?token=%s", frontendUrl, token);
        String htmlContent = String.format("""
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Đặt lại mật khẩu - LearnSpherel</title>
                </head>
                <body style="margin: 0; padding: 0; font-family: Arial, Helvetica, sans-serif; background-color: #F8F9FA; color: #333;">
                    <table align="center" border="0" cellpadding="0" cellspacing="0" width="100%%" style="max-width: 600px; background-color: #FFFFFF; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                        <tr>
                            <td style="padding: 20px; text-align: center; background-color: #007BFF; border-top-left-radius: 8px; border-top-right-radius: 8px;">
                                <h1 style="color: #FFFFFF; font-size: 24px; margin: 10px 0;">Đặt lại mật khẩu</h1>
                            </td>
                        </tr>
                        <tr>
                            <td style="padding: 30px;">
                                <h2 style="font-size: 20px; color: #333; margin-top: 0;">Xin chào,</h2>
                                <p style="font-size: 16px; line-height: 1.5; color: #555;">
                                    Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản LearnSpherel của mình. Vui lòng nhấp vào nút dưới đây để đặt lại mật khẩu:
                                </p>
                                <table align="center" border="0" cellpadding="0" cellspacing="0" style="margin: 20px 0;">
                                    <tr>
                                        <td style="background-color: #007BFF; border-radius: 5px; padding: 12px 24px;">
                                            <a href="%s" style="color: #FFFFFF; font-size: 16px; text-decoration: none; font-weight: bold;">Đặt lại mật khẩu</a>
                                        </td>
                                    </tr>
                                </table>
                                <p style="font-size: 14px; color: #555; line-height: 1.5;">
                                    Liên kết này sẽ hết hạn sau 15 phút. Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.
                                </p>
                                <p style="font-size: 14px; color: #555; line-height: 1.5;">
                                    Nếu bạn gặp bất kỳ vấn đề nào, hãy liên hệ với chúng tôi tại <a href="mailto:support@learnspherel.com" style="color: #007BFF; text-decoration: none;">support@learnspherel.com</a>.
                                </p>
                            </td>
                        </tr>
                        <tr>
                            <td style="padding: 20px; text-align: center; background-color: #F1F3F5; border-bottom-left-radius: 8px; border-bottom-right-radius: 8px;">
                                <p style="font-size: 12px; color: #777; margin: 0;">
                                    © 2025 LearnSpherel. All rights reserved.<br>
                                    <a href="https://learnspherel.com" style="color: #007BFF; text-decoration: none;">learnspherel.com</a> | 
                                    <a href="https://learnspherel.com/privacy" style="color: #007BFF; text-decoration: none;">Chính sách bảo mật</a>
                                </p>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """, resetUrl);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    private boolean isValidImageType(String contentType) {
        if (contentType == null) return false;
        return contentType.startsWith("image/") && (
                contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/gif") ||
                        contentType.equals("image/bmp")
        );
    }

    private long convertToBytes(String maxFileSize) {
        maxFileSize = maxFileSize.toUpperCase();
        if (maxFileSize.endsWith("MB")) {
            return Long.parseLong(maxFileSize.replace("MB", "").trim()) * 1024 * 1024;
        } else if (maxFileSize.endsWith("KB")) {
            return Long.parseLong(maxFileSize.replace("KB", "").trim()) * 1024;
        } else if (maxFileSize.endsWith("GB")) {
            return Long.parseLong(maxFileSize.replace("GB", "").trim()) * 1024 * 1024 * 1024;
        }
        return Long.parseLong(maxFileSize.trim());
    }

    private <T> ApiResponse<T> buildResponse(boolean isSuccess, HttpStatus status, String message, T data) {
        return ApiResponse.<T>builder()
                .success(isSuccess)
                .statusCode(status.value())
                .message(message)
                .data(data)
                .build();
    }
}