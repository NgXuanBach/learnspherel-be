package com.learnspherel.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnspherel.dto.ApiResponse;
import com.learnspherel.dto.ChuongTrinhDto;
import com.learnspherel.dto.LearningResponse;
import com.learnspherel.service.ChuongTrinhService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/chuong-trinh")
public class ChuongTrinhController {

    private final ChuongTrinhService chuongTrinhService;
    private final ObjectMapper objectMapper;

    public ChuongTrinhController(ChuongTrinhService chuongTrinhService, ObjectMapper objectMapper) {
        this.chuongTrinhService = chuongTrinhService;
        this.objectMapper = objectMapper;
    }
    @PostMapping
    public ResponseEntity<ApiResponse<ChuongTrinhDto>> createChuong(@RequestBody @Valid ChuongTrinhDto dto) {
        ApiResponse<ChuongTrinhDto> response = chuongTrinhService.createChuong(dto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @PutMapping("/{maChuongTrinh}")
    public ResponseEntity<ApiResponse<ChuongTrinhDto>> updateChuong(@PathVariable Long maChuongTrinh, @RequestBody @Valid ChuongTrinhDto dto) {
        ApiResponse<ChuongTrinhDto> response = chuongTrinhService.updateChuong(maChuongTrinh, dto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @DeleteMapping("/{maChuongTrinh}")
    public ResponseEntity<ApiResponse<Void>> deleteChuong(@PathVariable Long maChuongTrinh) {
        ApiResponse<Void> response = chuongTrinhService.deleteChuong(maChuongTrinh);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ChuongTrinhDto>> getChuongTrinh(@PathVariable Long id) {
        ApiResponse<ChuongTrinhDto> response = chuongTrinhService.getChuongTrinh(id);
        return new ResponseEntity<>(response, HttpStatus.resolve(response.getStatusCode()));
    }

    @GetMapping("/khoa-hoc/{maKhoaHoc}")
    public ResponseEntity<ApiResponse<List<ChuongTrinhDto>>> getChuongTrinhByKhoaHoc(@PathVariable Long maKhoaHoc) {
        ApiResponse<List<ChuongTrinhDto>> response = chuongTrinhService.getChuongTrinhByKhoaHoc(maKhoaHoc);
        return new ResponseEntity<>(response, HttpStatus.resolve(response.getStatusCode()));
    }

    @GetMapping("/khoa-hoc/{maKhoaHoc}/bai-hoc")
    public ResponseEntity<ApiResponse<LearningResponse>> getChuongHocWithBaiHoc(
            @PathVariable Long maKhoaHoc,
            HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        ApiResponse<LearningResponse> response = chuongTrinhService.getChuongHocWithBaiHoc(maKhoaHoc, token);
        return new ResponseEntity<>(response, HttpStatus.resolve(response.getStatusCode()));
    }

//    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<ApiResponse<ChuongTrinhDto>> updateChuongTrinh(
//            @PathVariable Long id,
//            @RequestPart("chuongTrinh") String chuongTrinhJson,
//            @RequestPart(value = "lessonFiles", required = false) List<MultipartFile> lessonFiles,
//            HttpServletRequest httpRequest) {
//        try {
//            String token = extractToken(httpRequest);
//            ChuongTrinhDto request = objectMapper.readValue(chuongTrinhJson, ChuongTrinhDto.class);
//            System.out.println(lessonFiles);
//            ApiResponse<ChuongTrinhDto> response = chuongTrinhService.updateChuongTrinh(id, request, token, lessonFiles);
//            return new ResponseEntity<>(response, HttpStatus.resolve(response.getStatusCode()));
//        } catch (Exception e) {
//            return new ResponseEntity<>(
//                    ApiResponse.<ChuongTrinhDto>builder()
//                            .success(false)
//                            .statusCode(HttpStatus.BAD_REQUEST.value())
//                            .message(e.getMessage())
//                            .data(null)
//                            .timestamp(System.currentTimeMillis())
//                            .build(),
//                    HttpStatus.BAD_REQUEST);
//        }
//    }

    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<ChuongTrinhDto>>> createOrUpdateChuongTrinhs(
            @RequestPart("chuongTrinhs") String chuongTrinhsJson,
            @RequestPart(value = "lessonFiles", required = false) List<MultipartFile> lessonFiles,
            HttpServletRequest httpRequest) {
        try {

            String token = extractToken(httpRequest);
            List<ChuongTrinhDto> requests = objectMapper.readValue(chuongTrinhsJson, new TypeReference<List<ChuongTrinhDto>>() {
            });
            ApiResponse<List<ChuongTrinhDto>> response = chuongTrinhService.createOrUpdateChuongTrinhs(requests, token, lessonFiles);
            return new ResponseEntity<>(response, HttpStatus.resolve(response.getStatusCode()));
        } catch (Exception e) {
            return new ResponseEntity<>(
                    ApiResponse.<List<ChuongTrinhDto>>builder()
                            .success(false)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .data(null)
                            .timestamp(System.currentTimeMillis())
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }



    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}