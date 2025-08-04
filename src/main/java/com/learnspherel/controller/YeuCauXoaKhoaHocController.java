package com.learnspherel.controller;

import com.learnspherel.dto.ApiResponse;
import com.learnspherel.dto.YeuCauXoaKhoaHocDto;
import com.learnspherel.dto.YeuCauXoaKhoaHocRequest;
import com.learnspherel.entity.enums.TrangThaiYeuCau;
import com.learnspherel.service.YeuCauXoaKhoaHocService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/yeu-cau-xoa-khoa-hoc")
@RequiredArgsConstructor
public class YeuCauXoaKhoaHocController {

    private final YeuCauXoaKhoaHocService yeuCauXoaKhoaHocService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> taoYeuCauXoa(@RequestBody @Valid YeuCauXoaKhoaHocRequest request) {
        ApiResponse<Void> res = yeuCauXoaKhoaHocService.taoYeuCauXoa(request);
        return new ResponseEntity<>(res, HttpStatus.valueOf(res.getStatusCode()));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<YeuCauXoaKhoaHocDto>>> getAllYeuCau(
            @RequestParam(required = false) TrangThaiYeuCau trangThai
    ) {
        ApiResponse<List<YeuCauXoaKhoaHocDto>> res = yeuCauXoaKhoaHocService.getAllYeuCau(trangThai);
        return new ResponseEntity<>(res, HttpStatus.valueOf(res.getStatusCode()));
    }

    @PostMapping("/{maYeuCau}/phe-duyet")
    public ResponseEntity<ApiResponse<Void>> pheDuyet(@PathVariable Long maYeuCau) {
        ApiResponse<Void> res = yeuCauXoaKhoaHocService.pheDuyetYeuCau(maYeuCau, true);
        return new ResponseEntity<>(res, HttpStatus.valueOf(res.getStatusCode()));
    }

    @PostMapping("/{maYeuCau}/tu-choi")
    public ResponseEntity<ApiResponse<Void>> tuChoi(@PathVariable Long maYeuCau, @RequestBody(required = false) Map<String, String> body) {
        String lyDoTuChoi = body != null ? body.get("lyDoTuChoi") : null;
        ApiResponse<Void> res = yeuCauXoaKhoaHocService.pheDuyetYeuCau(maYeuCau, false, lyDoTuChoi);
        return new ResponseEntity<>(res, HttpStatus.valueOf(res.getStatusCode()));
    }

    // Có thể bổ sung API duyệt / từ chối yêu cầu nếu muốn
}
