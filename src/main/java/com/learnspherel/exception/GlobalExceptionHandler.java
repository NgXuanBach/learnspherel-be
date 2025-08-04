package com.learnspherel.exception;

import com.learnspherel.dto.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final MessageSource messageSource;
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        logger.warn("Validation error: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST,
                messageSource.getMessage("auth.validation.failed", null, "Validation failed", null),
                errors);
    }

    @ExceptionHandler(InvalidJwtException.class)
    public ResponseEntity<ApiResponse> handleInvalidJwtException(InvalidJwtException ex) {
        logger.warn("Invalid JWT token: {}", ex.getMessage());
        String messageKey = ex.getCause() instanceof ExpiredJwtException ? "auth.jwt.expired" : "auth.jwt.invalid";
        return buildResponse(HttpStatus.UNAUTHORIZED,
                messageSource.getMessage(messageKey, null, ex.getMessage(), null),
                Collections.singletonMap("error", ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = messageSource.getMessage("auth.data.integrity.error", null, "Data integrity violation", null);
        logger.error("Data integrity violation: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.CONFLICT, message, ex);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        logger.warn("HTTP method not supported: {}", ex.getMessage());
        String supportedMethods = String.join(", ", ex.getSupportedMethods());
        String errorMessage = messageSource.getMessage("http.method.not.supported", new Object[]{supportedMethods},
                "Method not supported. Supported methods are: " + supportedMethods, null);
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, errorMessage, null);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse> handleIllegalStateException(IllegalStateException ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(), ex);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> handleBadCredentialsException(BadCredentialsException ex) {
        logger.warn("Bad credentials provided: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED,
                messageSource.getMessage("auth.bad.credentials", null, "Invalid username or password", null),
                Collections.singletonMap("error", "BadCredentials"));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        logger.warn("User not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED,
                messageSource.getMessage("auth.login.username.notfound", null, "Username not found", null),
                Collections.singletonMap("error", ex.getMessage()));
    }

    @ExceptionHandler(KhoaHocNotFoundException.class)
    public ResponseEntity<ApiResponse> handleKhoaHocNotFoundException(KhoaHocNotFoundException ex) {
        logger.warn("Course not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND,
                messageSource.getMessage("khoahoc.notfound", null, "Course not found", null),
                Collections.singletonMap("error", ex.getMessage()));
    }

    @ExceptionHandler(BaiHocNotFoundException.class)
    public ResponseEntity<ApiResponse> handleBaiHocNotFoundException(BaiHocNotFoundException ex) {
        logger.warn("Lesson not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND,
                messageSource.getMessage("baihoc.notfound", null, "Lesson not found", null),
                Collections.singletonMap("error", ex.getMessage()));
    }

    @ExceptionHandler(BaiKiemTraNotFoundException.class)
    public ResponseEntity<ApiResponse> handleBaiKiemTraNotFoundException(BaiKiemTraNotFoundException ex) {
        logger.warn("Test not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND,
                messageSource.getMessage("bai_kiem_tra.notfound", null, "Test not found", null),
                Collections.singletonMap("error", ex.getMessage()));
    }

    @ExceptionHandler(TaiLieuNotFoundException.class)
    public ResponseEntity<ApiResponse> handleTaiLieuNotFoundException(TaiLieuNotFoundException ex) {
        logger.warn("Document not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND,
                messageSource.getMessage("tailieu.notfound", null, "Document not found", null),
                Collections.singletonMap("error", ex.getMessage()));
    }

    @ExceptionHandler(ChuongTrinhNotFoundException.class)
    public ResponseEntity<ApiResponse> handleChuongTrinhNotFoundException(ChuongTrinhNotFoundException ex) {
        logger.warn("Program not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND,
                messageSource.getMessage("chuongtrinh.notfound", null, "Không tìm thấy chương trình", null),
                Collections.singletonMap("error", ex.getMessage()));
    }

    @ExceptionHandler(PasswordResetTokenInvalidException.class)
    public ResponseEntity<ApiResponse> handlePasswordResetTokenInvalidException(PasswordResetTokenInvalidException ex) {
        logger.warn("Invalid reset token: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                Collections.singletonMap("error", ex.getMessage()));
    }

    @ExceptionHandler(GioHangNotFoundException.class)
    public ResponseEntity<ApiResponse> handleGioHangNotFoundException(GioHangNotFoundException ex) {
        logger.warn("Cart item not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND,
                messageSource.getMessage("gio_hang.notfound", null, "Không tìm thấy mục giỏ hàng", null),
                Collections.singletonMap("error", ex.getMessage()));
    }

    @ExceptionHandler(ThanhToanNotFoundException.class)
    public ResponseEntity<ApiResponse> handleThanhToanNotFoundException(ThanhToanNotFoundException ex) {
        logger.warn("Payment not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND,
                messageSource.getMessage("thanh_toan.notfound", null, "Không tìm thấy giao dịch thanh toán", null),
                Collections.singletonMap("error", ex.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        logger.warn("File size exceeded: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST,
                messageSource.getMessage("nguoidung.upload_anh.file.too.large", null, "File size exceeds the maximum limit", null),
                null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        logger.warn("Missing request parameter: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST,
                messageSource.getMessage("thanh_toan.parameter.missing", null, "Thiếu tham số yêu cầu: " + ex.getParameterName(), null),
                Collections.singletonMap("error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                messageSource.getMessage("auth.unexpected.error", null, "Unexpected error occurred", null),
                ex);
    }

    @ExceptionHandler(KyNangAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleKyNangAlreadyExistsException(KyNangAlreadyExistsException ex) {
        logger.warn("Skill already exists: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT,
                messageSource.getMessage("kynang.already.exists", null, "Skill already exists", null),
                Collections.singletonMap("error", ex.getMessage()));
    }

    @ExceptionHandler(CertificateNotFoundException.class)
    public ResponseEntity<ApiResponse> handleCertificateNotFoundException(CertificateNotFoundException ex) {
        logger.warn("Certificate not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND,
                messageSource.getMessage("certificate.notfound", null, "Certificate not found", null),
                Collections.singletonMap("error", ex.getMessage()));
    }

    @ExceptionHandler(CertificateAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleCertificateAlreadyExistsException(CertificateAlreadyExistsException ex) {
        logger.warn("Certificate already exists: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT,
                messageSource.getMessage("certificate.already.exists", null, "Certificate already exists", null),
                Collections.singletonMap("error", ex.getMessage()));
    }
    @ExceptionHandler(ReviewTeacherDuplicateException.class)
    public ResponseEntity<ApiResponse> handleReviewTeacherDuplicateException(ReviewTeacherDuplicateException ex) {
        logger.warn("Đánh giá giảng viên trùng lặp: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT,
                messageSource.getMessage("teacher.review.duplicate", null, ex.getMessage(), null),
                Collections.singletonMap("error", ex.getMessage()));
    }
    @ExceptionHandler(YeuCauXoaKhoaHocNotFound.class)
    public ResponseEntity<ApiResponse> handleYeuCauXoaKhoaHocException(YeuCauXoaKhoaHocNotFound ex) {
        logger.warn("Yêu cầu xoá khoá học không tìm thấy: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT,
                messageSource.getMessage("yeucau.notfound", null, ex.getMessage(), null),
                Collections.singletonMap("error", ex.getMessage()));
    }
    private ResponseEntity<ApiResponse> buildResponse(HttpStatus status, String message, Object data) {
        return ResponseEntity.status(status).body(ApiResponse.builder()
                .success(false)
                .statusCode(status.value())
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build());
    }
}