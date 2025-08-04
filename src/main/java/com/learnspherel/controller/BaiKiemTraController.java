package com.learnspherel.controller;

import com.learnspherel.dto.ApiResponse;
import com.learnspherel.dto.BaiKiemTraDto;
import com.learnspherel.entity.enums.LoaiBaiKiemTra;
import com.learnspherel.service.BaiKiemTraService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bai-kiem-tra")
public class BaiKiemTraController {

    private final BaiKiemTraService baiKiemTraService;

    public BaiKiemTraController(BaiKiemTraService baiKiemTraService) {
        this.baiKiemTraService = baiKiemTraService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BaiKiemTraDto>> createBaiKiemTra(@Valid @RequestBody BaiKiemTraDto baiKiemTraDto) {
        ApiResponse<BaiKiemTraDto> response = baiKiemTraService.createBaiKiemTra(baiKiemTraDto);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{id}/khoa-hoc/{maKhoaHoc}")
    public ResponseEntity<ApiResponse<BaiKiemTraDto>> getBaiKiemTraById(@PathVariable Long id, @PathVariable Long maKhoaHoc) {
        ApiResponse<BaiKiemTraDto> response = baiKiemTraService.getBaiKiemTraById(id, maKhoaHoc);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/khoa-hoc/{maKhoaHoc}/loai/{loai}")
    public ResponseEntity<ApiResponse<List<BaiKiemTraDto>>> getAllBaiKiemTraByKhoaHocAndLoai(
            @PathVariable Long maKhoaHoc, @PathVariable LoaiBaiKiemTra loai) {
        ApiResponse<List<BaiKiemTraDto>> response = baiKiemTraService.getAllBaiKiemTraByKhoaHocAndLoai(maKhoaHoc, loai);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BaiKiemTraDto>> updateBaiKiemTra(@PathVariable Long id, @Valid @RequestBody BaiKiemTraDto baiKiemTraDto) {
        ApiResponse<BaiKiemTraDto> response = baiKiemTraService.updateBaiKiemTra(id, baiKiemTraDto);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBaiKiemTra(@PathVariable Long id) {
        ApiResponse<Void> response = baiKiemTraService.deleteBaiKiemTra(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}