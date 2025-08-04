package com.learnspherel.controller;

import com.learnspherel.dto.ApiResponse;
import com.learnspherel.dto.BaiHocDto;
import com.learnspherel.service.BaiHocService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/bai-hoc")
//@AllArgsConstructor
public class BaiHocController {

    private final BaiHocService baiHocService;

    public BaiHocController(BaiHocService baiHocService) {
        this.baiHocService = baiHocService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BaiHocDto>> createBaiHoc(@Valid @RequestBody BaiHocDto baiHocDto) {
        ApiResponse<BaiHocDto> response = baiHocService.createBaiHoc(baiHocDto);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BaiHocDto>> getBaiHocById(@PathVariable Long id) {
        ApiResponse<BaiHocDto> response = baiHocService.getBaiHocById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/khoa-hoc/{maKhoaHoc}")
    public ResponseEntity<ApiResponse<List<BaiHocDto>>> getAllBaiHocByKhoaHoc(@PathVariable Long maKhoaHoc) {
        ApiResponse<List<BaiHocDto>> response = baiHocService.getAllBaiHocByKhoaHoc(maKhoaHoc);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BaiHocDto>> updateBaiHoc(@PathVariable Long id, @Valid @RequestBody BaiHocDto baiHocDto) {
        ApiResponse<BaiHocDto> response = baiHocService.updateBaiHoc(id, baiHocDto);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBaiHoc(@PathVariable Long id) {
        ApiResponse<Void> response = baiHocService.deleteBaiHoc(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    // API lấy danh sách bài học theo mã chương
    @GetMapping("/chuong/{maChuongTrinh}")
    public ResponseEntity<ApiResponse<List<BaiHocDto>>> getLessonsByChuong(@PathVariable Long maChuongTrinh) {
        ApiResponse<List<BaiHocDto>> res = baiHocService.getLessonsByChuong(maChuongTrinh);
        return new ResponseEntity<>(res, HttpStatus.valueOf(res.getStatusCode()));
    }

    // API batch cập nhật/tạo danh sách bài học cho chương, gồm upload video (multipart)
    @PostMapping(value = "/chuong/{maChuongTrinh}/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> batchLessons(
            @PathVariable Long maChuongTrinh,
            @RequestPart("lessons") String lessonsJson, // list BaiHocDto ở dạng JSON
            @RequestPart(value = "videos", required = false) List<MultipartFile> videos) throws IOException {
        ApiResponse<Void> res = baiHocService.batchLessons(maChuongTrinh, lessonsJson, videos);
        return new ResponseEntity<>(res, HttpStatus.valueOf(res.getStatusCode()));
    }
}