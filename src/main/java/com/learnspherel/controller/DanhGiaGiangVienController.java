package com.learnspherel.controller;

import com.learnspherel.dto.ReviewTeacherRequestDTO;
import com.learnspherel.dto.ReviewTeacherResponseDTO;
import com.learnspherel.service.DanhGiaGiangVienService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/danh-gia-giang-vien")
@RequiredArgsConstructor
public class DanhGiaGiangVienController {

    private final DanhGiaGiangVienService service;

    @PostMapping
    public ResponseEntity<?> reviewTeacher(@RequestBody @Valid ReviewTeacherRequestDTO dto, Locale locale) {
        service.reviewTeacher(dto, locale);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/giang-vien/{maGiangVien}")
    public ResponseEntity<List<ReviewTeacherResponseDTO>> getAllByMaGiangVien(@PathVariable Long maGiangVien) {
        return ResponseEntity.ok(service.getAllByMaGiangVien(maGiangVien));
    }
}
