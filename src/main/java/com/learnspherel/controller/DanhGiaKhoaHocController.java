package com.learnspherel.controller;

import com.learnspherel.dto.ReviewCourseRequestDTO;
import com.learnspherel.dto.ReviewCourseResponseDTO;
import com.learnspherel.service.DanhGiaKhoaHocService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;


@RestController
@RequestMapping("/api/danh-gia-khoa-hoc")
@RequiredArgsConstructor
public class DanhGiaKhoaHocController {

    private final DanhGiaKhoaHocService danhGiaKhoaHocService;

    @PostMapping
    public ResponseEntity<?> reviewCourse(
            @RequestBody @Valid ReviewCourseRequestDTO dto, Locale locale) {
        danhGiaKhoaHocService.reviewCourse(dto, locale);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/khoa-hoc/{maKhoaHoc}")
    public ResponseEntity<List<ReviewCourseResponseDTO>> getAllReviewByKhoaHoc(@PathVariable Long maKhoaHoc) {
        return ResponseEntity.ok(danhGiaKhoaHocService.getAllByKhoaHoc(maKhoaHoc));
    }

}

