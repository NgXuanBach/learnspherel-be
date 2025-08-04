package com.learnspherel.controller;

import com.learnspherel.dto.*;
import com.learnspherel.service.KhoaHocService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/khoa-hoc")
public class KhoaHocController {

    private static final Logger logger = LoggerFactory.getLogger(KhoaHocController.class);

    private final KhoaHocService khoaHocService;

    public KhoaHocController(KhoaHocService khoaHocService) {
        this.khoaHocService = khoaHocService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<KhoaHocResponse>>> getAllKhoaHoc() {
        logger.info("Yêu cầu lấy tất cả khóa học");
        ApiResponse<List<KhoaHocResponse>> response = khoaHocService.getAllKhoaHoc();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<KhoaHocResponse>> getKhoaHocById(@PathVariable Long id) {
        logger.info("Yêu cầu lấy khóa học ID {}", id);
        ApiResponse<KhoaHocResponse> response = khoaHocService.getKhoaHocById(id);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<KhoaHocResponse>> createKhoaHoc(@Valid @RequestBody KhoaHocRequest request, BindingResult bindingResult) {
        logger.info("Yêu cầu tạo khóa học: {}", request.getTieuDe());
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            logger.warn("Lỗi validation khi tạo khóa học: {}", errorMessage);
            return new ResponseEntity<>(
                    ApiResponse.<KhoaHocResponse>builder()
                            .success(false)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .message(errorMessage)
                            .data(null)
                            .timestamp(System.currentTimeMillis())
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
        ApiResponse<KhoaHocResponse> response = khoaHocService.createKhoaHoc(request);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<KhoaHocResponse>> updateKhoaHoc(@PathVariable Long id, @Valid @RequestBody KhoaHocRequest request, BindingResult bindingResult) {
        logger.info("Yêu cầu cập nhật khóa học ID {}: {}", id, request.getTieuDe());
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            logger.warn("Lỗi validation khi cập nhật khóa học ID {}: {}", id, errorMessage);
            return new ResponseEntity<>(
                    ApiResponse.<KhoaHocResponse>builder()
                            .success(false)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .message(errorMessage)
                            .data(null)
                            .timestamp(System.currentTimeMillis())
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
        ApiResponse<KhoaHocResponse> response = khoaHocService.updateKhoaHoc(id, request);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteKhoaHoc(@PathVariable Long id) {
        logger.info("Yêu cầu xóa khóa học ID {}", id);
        ApiResponse<Void> response = khoaHocService.deleteKhoaHoc(id);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    //    @PostMapping("/{id}/ky-nang/{kyNangId}")
//    public ResponseEntity<ApiResponse<Void>> addKyNangToKhoaHoc(@PathVariable Long id, @PathVariable Long kyNangId) {
//        logger.info("Yêu cầu thêm kỹ năng ID {} vào khóa học ID {}", kyNangId, id);
//        ApiResponse<Void> response = khoaHocService.addKyNangToKhoaHoc(id, kyNangId);
//        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
//    }
    @PutMapping("/{id}/request-approval")
    public ResponseEntity<ApiResponse<KhoaHocResponse>> requestApproval(@PathVariable Long id) {
        ApiResponse<KhoaHocResponse> response = khoaHocService.requestApproval(id);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @GetMapping("/pending-approval")
    public ResponseEntity<ApiResponse<List<KhoaHocResponse>>> getPendingApprovalCourses() {
        ApiResponse<List<KhoaHocResponse>> response = khoaHocService.getPendingApprovalCourses();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @PostMapping("/{id}/gio-hang")
    public ResponseEntity<ApiResponse<Void>> themVaoGioHang(@PathVariable Long id, HttpServletRequest httpRequest) {
        logger.info("Yêu cầu thêm khóa học ID {} vào giỏ hàng", id);
        String token = extractToken(httpRequest);
        ApiResponse<Void> response = khoaHocService.themVaoGioHang(id, token);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @PostMapping(value = "/{id}/upload-anh", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<KhoaHocResponse>> uploadAnhDaiDien(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file,
            HttpServletRequest httpRequest) {
        logger.info("Yêu cầu upload ảnh đại diện cho khóa học ID {}", id);
        String token = extractToken(httpRequest);
        ApiResponse<KhoaHocResponse> response = khoaHocService.uploadAnhDaiDien(id, file);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @PostMapping(value = "/{id}/upload-video-demo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<KhoaHocResponse>> uploadVideoDemo(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file,
            HttpServletRequest httpRequest) {
        logger.info("Yêu cầu upload video demo cho khóa học ID {}", id);
        String token = extractToken(httpRequest);
        ApiResponse<KhoaHocResponse> response = khoaHocService.uploadVideoDemo(id, file);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @GetMapping("/{id}/video-demo")
    public ResponseEntity<ApiResponse<String>> getVideoDemoUrl(@PathVariable Long id) {
        logger.info("Yêu cầu lấy URL video demo cho khóa học ID {}", id);
        ApiResponse<String> response = khoaHocService.getVideoDemoUrl(id);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @GetMapping("/my-courses")
    public ResponseEntity<ApiResponse<List<MyCourseResponse>>> getMyCourses(HttpServletRequest httpRequest) {
        logger.info("Yêu cầu lấy danh sách khóa học của người dùng");
        String token = extractToken(httpRequest);
        ApiResponse<List<MyCourseResponse>> response = khoaHocService.getMyCourses(token);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @GetMapping("/instructor-courses")
    public ResponseEntity<ApiResponse<List<KhoaHocResponse>>> getCoursesByInstructor(HttpServletRequest httpRequest) {
        logger.info("Yêu cầu lấy danh sách khóa học của giảng viên");
        String token = extractToken(httpRequest);
        ApiResponse<List<KhoaHocResponse>> response = khoaHocService.getCoursesByInstructorToken(token);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @GetMapping("/revenue-by-month")
    public ResponseEntity<ApiResponse<List<RevenueByMonthResponse>>> getRevenueByMonth(
            HttpServletRequest httpRequest,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        logger.info("Yêu cầu lấy doanh thu theo tháng, year: {}, month: {}", year, month);
        String token = extractToken(httpRequest);
        ApiResponse<List<RevenueByMonthResponse>> response = khoaHocService.getRevenueByMonth(token, year, month);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }
    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<KhoaHocResponse>> approveCourse(@PathVariable Long id) {
        ApiResponse<KhoaHocResponse> response = khoaHocService.approveCourse(id);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<KhoaHocResponse>> rejectCourse(@PathVariable Long id) {
        ApiResponse<KhoaHocResponse> response = khoaHocService.rejectCourse(id);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    @PostMapping(value = "/{id}/upload-media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<KhoaHocResponse>> uploadMedia(
            @PathVariable Long id,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart(value = "videoDemo", required = false) MultipartFile videoDemo,
            HttpServletRequest httpRequest) {
        // xử lý upload từng file nếu có
        ApiResponse<KhoaHocResponse> response = khoaHocService.uploadMedia(id, thumbnail, videoDemo);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

}