package com.learnspherel.controller;

import com.learnspherel.dto.ApiResponse;
import com.learnspherel.dto.TaiLieuDto;
import com.learnspherel.entity.TaiLieu;
import com.learnspherel.repository.TaiLieuRepository;
import com.learnspherel.service.TaiLieuService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/tai-lieu")
@AllArgsConstructor
public class TaiLieuController {

    private final TaiLieuService taiLieuService;
    private final TaiLieuRepository taiLieuRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<TaiLieuDto>> createTaiLieu(@Valid @RequestBody TaiLieuDto taiLieuDto) {
        ApiResponse<TaiLieuDto> response = taiLieuService.createTaiLieu(taiLieuDto);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<TaiLieuDto>> uploadTaiLieu(@RequestPart("file") MultipartFile file,
                                                     @RequestPart("taiLieuDto") @Valid TaiLieuDto taiLieuDto) {
        ApiResponse<TaiLieuDto> response = taiLieuService.uploadTaiLieu(file, taiLieuDto);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaiLieuDto>> getTaiLieuById(@PathVariable Long id) {
        ApiResponse<TaiLieuDto> response = taiLieuService.getTaiLieuById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/bai-hoc/{maBaiHoc}")
    public ResponseEntity<ApiResponse<List<TaiLieuDto>>> getTaiLieuByBaiHoc(@PathVariable Long maBaiHoc,
                                                          @RequestParam(required = false) String loaiFile) {
        ApiResponse<List<TaiLieuDto>> response = taiLieuService.getTaiLieuByBaiHoc(maBaiHoc, loaiFile);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        Resource resource = taiLieuService.getDownloadFile(id);
        TaiLieu taiLieu = taiLieuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        String contentType = switch (taiLieu.getLoaiFile().toString().toUpperCase()) {
            case "VIDEO" -> "video/mp4";
            case "PDF" -> "application/pdf";
            case "IMAGE" -> "image/jpeg";
            case "CODE" -> "text/plain";
            default -> "application/octet-stream";
        };
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + taiLieu.getTenFile() + "\"")
                .body(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaiLieuDto>> updateTaiLieu(@PathVariable Long id, @Valid @RequestBody TaiLieuDto taiLieuDto) {
        ApiResponse<TaiLieuDto> response = taiLieuService.updateTaiLieu(id, taiLieuDto);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteTaiLieu(@PathVariable Long id) {
        ApiResponse<?> response = taiLieuService.deleteTaiLieu(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}