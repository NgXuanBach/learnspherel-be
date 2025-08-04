//package com.learnspherel.service;
//
//import com.learnspherel.config.FileStorageProperties;
//import com.learnspherel.dto.ApiResponse;
//import com.learnspherel.dto.UserResponse;
//import com.learnspherel.entity.NguoiDung;
//import com.learnspherel.mapper.UserMapper;
//import com.learnspherel.repository.NguoiDungRepository;
//import com.learnspherel.utils.JwtUtil;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.MessageSource;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardOpenOption;
//import java.util.UUID;
//
//@Service
//public class NguoiDungService {
//
//    private final NguoiDungRepository nguoiDungRepository;
//    private final UserMapper nguoiDungMapper;
//    private final MessageSource messageSource;
//    private final JwtUtil jwtUtil;
//    private final FileStorageProperties fileStorageProperties;
//    @Value("${learnspherel.upload.dir}")
//    private String uploadDirBase;
//
//    public NguoiDungService(NguoiDungRepository nguoiDungRepository,
//                            UserMapper nguoiDungMapper,
//                            MessageSource messageSource,
//                            JwtUtil jwtUtil,
//                            FileStorageProperties fileStorageProperties) {
//        this.nguoiDungRepository = nguoiDungRepository;
//        this.nguoiDungMapper = nguoiDungMapper;
//        this.messageSource = messageSource;
//        this.jwtUtil = jwtUtil;
//        this.fileStorageProperties = fileStorageProperties;
//    }
//
//    @Transactional(readOnly = true)
//    public ApiResponse<UserResponse> getNguoiDungByToken(String token) {
//        if (token == null || token.isEmpty()) {
//            return buildResponse(false, HttpStatus.UNAUTHORIZED,
//                    messageSource.getMessage("auth.authenticate.invalid", null, "Token không hợp lệ",null), null);
//        }
//
//        String username = jwtUtil.extractUsername(token);
//        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhapOrEmail(username, username)
//                .orElseThrow(() -> new UsernameNotFoundException("Người dùng không tồn tại"));
//        UserResponse response = nguoiDungMapper.toDto(nguoiDung);
//        return buildResponse(true, HttpStatus.OK,
//                messageSource.getMessage("nguoidung.get.success", null, "Lấy thông tin người dùng thành công",null), response);
//    }
//
//    @Transactional
//    public ApiResponse<UserResponse> uploadAnhDaiDien(MultipartFile file, String token) {
//        try {
//            if (token == null || token.isEmpty()) {
//                return buildResponse(false, HttpStatus.UNAUTHORIZED,
//                        messageSource.getMessage("auth.authenticate.invalid", null, "Token không hợp lệ",null), null);
//            }
//
//            String username = jwtUtil.extractUsername(token);
//            NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhapOrEmail(username, username)
//                    .orElseThrow(() -> new UsernameNotFoundException("Người dùng không tồn tại"));
//
//            if (file == null || file.isEmpty()) {
//                return buildResponse(false, HttpStatus.BAD_REQUEST,
//                        messageSource.getMessage("nguoidung.upload_anh.file.empty", null, "File ảnh không được để trống",null), null);
//            }
//
//            long maxSizeInBytes = convertToBytes(fileStorageProperties.getMaxFileSize());
//            if (file.getSize() > maxSizeInBytes) {
//                return buildResponse(false, HttpStatus.BAD_REQUEST,
//                        messageSource.getMessage("nguoidung.upload_anh.file.too.large", new Object[]{fileStorageProperties.getMaxFileSize()}, "File ảnh vượt quá kích thước tối đa",null), null);
//            }
//
//            String contentType = file.getContentType();
//            if (!isValidImageType(contentType)) {
//                return buildResponse(false, HttpStatus.BAD_REQUEST,
//                        messageSource.getMessage("nguoidung.upload_anh.file.invalid", null, "Định dạng file ảnh không hợp lệ",null), null);
//            }
//
//            String originalFilename = file.getOriginalFilename();
//            if (originalFilename == null || originalFilename.isEmpty()) {
//                throw new IllegalArgumentException("Tên file không hợp lệ");
//            }
//
//            String uploadDir = uploadDirBase + "images/";
//            Path uploadPath = Paths.get(uploadDir);
//            if (!Files.exists(uploadPath)) {
//                Files.createDirectories(uploadPath);
//            }
//
//            String fileName = UUID.randomUUID() + "_" + originalFilename;
//            Path filePath = uploadPath.resolve(fileName);
//            Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
//
//            // Xóa ảnh cũ nếu có
//            if (nguoiDung.getAnhDaiDien() != null && !nguoiDung.getAnhDaiDien().isEmpty()) {
//                String oldFilePath = uploadDirBase + nguoiDung.getAnhDaiDien().substring(1);
//                Files.deleteIfExists(Paths.get(oldFilePath));
//            }
//
//            nguoiDung.setAnhDaiDien("/uploads/images/" + fileName);
//            nguoiDung = nguoiDungRepository.save(nguoiDung);
//            UserResponse response = nguoiDungMapper.toDto(nguoiDung);
//
//            return buildResponse(true, HttpStatus.OK,
//                    messageSource.getMessage("nguoidung.upload_anh.success", null, "Upload ảnh đại diện thành công",null), response);
//        } catch (IOException e) {
//            throw new RuntimeException(
//                    messageSource.getMessage("nguoidung.upload_anh.failure", null, "Upload ảnh đại diện thất bại",null), e);
//        }
//    }
//
//    private boolean isValidImageType(String contentType) {
//        if (contentType == null) return false;
//        return contentType.startsWith("image/") && (
//                contentType.equals("image/jpeg") ||
//                        contentType.equals("image/png") ||
//                        contentType.equals("image/gif") ||
//                        contentType.equals("image/bmp")
//        );
//    }
//
//    private long convertToBytes(String maxFileSize) {
//        maxFileSize = maxFileSize.toUpperCase();
//        if (maxFileSize.endsWith("MB")) {
//            return Long.parseLong(maxFileSize.replace("MB", "").trim()) * 1024 * 1024;
//        } else if (maxFileSize.endsWith("KB")) {
//            return Long.parseLong(maxFileSize.replace("KB", "").trim()) * 1024;
//        } else if (maxFileSize.endsWith("GB")) {
//            return Long.parseLong(maxFileSize.replace("GB", "").trim()) * 1024 * 1024 * 1024;
//        }
//        return Long.parseLong(maxFileSize.trim());
//    }
//
//    private <T> ApiResponse<T> buildResponse(boolean success, HttpStatus status, String message, T data) {
//        return ApiResponse.<T>builder()
//                .success(success)
//                .statusCode(status.value())
//                .message(message)
//                .data(data)
//                .timestamp(System.currentTimeMillis())
//                .build();
//    }
//}