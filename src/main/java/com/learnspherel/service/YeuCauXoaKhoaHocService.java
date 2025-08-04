package com.learnspherel.service;

import com.learnspherel.dto.ApiResponse;
import com.learnspherel.dto.YeuCauXoaKhoaHocDto;
import com.learnspherel.dto.YeuCauXoaKhoaHocRequest;
import com.learnspherel.entity.KhoaHoc;
import com.learnspherel.entity.NguoiDung;
import com.learnspherel.entity.YeuCauXoaKhoaHoc;
import com.learnspherel.entity.enums.TrangThaiKhoaHoc;
import com.learnspherel.entity.enums.TrangThaiYeuCau;
import com.learnspherel.exception.KhoaHocNotFoundException;
import com.learnspherel.exception.UsernameNotFoundException;
import com.learnspherel.exception.YeuCauXoaKhoaHocNotFound;
import com.learnspherel.repository.KhoaHocRepository;
import com.learnspherel.repository.NguoiDungRepository;
import com.learnspherel.repository.YeuCauXoaKhoaHocRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class YeuCauXoaKhoaHocService {

    private final KhoaHocRepository khoaHocRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final YeuCauXoaKhoaHocRepository yeuCauXoaKhoaHocRepository;
    private final MessageSource messageSource;

    @Transactional
    public ApiResponse<Void> taoYeuCauXoa(YeuCauXoaKhoaHocRequest req) {
        // Kiểm tra hợp lệ
        KhoaHoc khoaHoc = khoaHocRepository.findById(req.getMaKhoaHoc())
                .orElseThrow(() -> new KhoaHocNotFoundException("Không tìm thấy khoá học!"));
        NguoiDung giangVien = nguoiDungRepository.findById(req.getMaGiangVien())
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy giảng viên!"));

        // Chỉ cho phép tạo yêu cầu nếu chưa có yêu cầu đang chờ xử lý
        boolean daTonTai = yeuCauXoaKhoaHocRepository.existsByKhoaHocAndTrangThai(
                khoaHoc, TrangThaiYeuCau.CHO_DUYET
        );
        if (daTonTai) {
            return buildResponse(false, HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("yeucau.xoa.exists", null, "Đã tồn tại yêu cầu xoá khoá học đang chờ xử lý!", null), null);
        }
        YeuCauXoaKhoaHoc yeuCau = new YeuCauXoaKhoaHoc();
        yeuCau.setKhoaHoc(khoaHoc);
        yeuCau.setGiangVien(giangVien);
        yeuCau.setLyDo(req.getLyDo());
        yeuCau.setTrangThai(TrangThaiYeuCau.CHO_DUYET);
        yeuCauXoaKhoaHocRepository.save(yeuCau);
        return buildResponse(true, HttpStatus.OK,
                messageSource.getMessage("yeucau.xoa.success", null, "Gửi yêu cầu xoá khoá học thành công!", null), null);
    }

    @Transactional
    public ApiResponse<Void> pheDuyetYeuCau(Long maYeuCau, boolean isApprove, String... lyDoTuChoi) {
        YeuCauXoaKhoaHoc yeuCau = yeuCauXoaKhoaHocRepository.findById(maYeuCau)
                .orElseThrow(() -> new YeuCauXoaKhoaHocNotFound("Không tìm thấy yêu cầu xoá!"));

        if (yeuCau.getTrangThai() != TrangThaiYeuCau.CHO_DUYET) {
            return buildResponse(false, HttpStatus.BAD_REQUEST, "Yêu cầu đã được xử lý!", null);
        }

        if (isApprove) {
            yeuCau.setTrangThai(TrangThaiYeuCau.DA_DUYET);
            // Tiến hành khoá học: có thể xoá, hoặc đổi trạng thái bị ẩn tuỳ logic
            KhoaHoc khoaHoc = yeuCau.getKhoaHoc();
            khoaHoc.setTrangThai(TrangThaiKhoaHoc.DA_XOA);
            khoaHocRepository.save(khoaHoc);
        } else {
            yeuCau.setTrangThai(TrangThaiYeuCau.TU_CHOI);
            if (lyDoTuChoi.length > 0) {
                yeuCau.setLyDoTuChoi(lyDoTuChoi[0]);
            }
        }
        yeuCau.setNgayCapNhat(LocalDateTime.now());
        yeuCauXoaKhoaHocRepository.save(yeuCau);
        return buildResponse(true, HttpStatus.OK, isApprove ? "Đã phê duyệt yêu cầu xoá!" : "Đã từ chối yêu cầu xoá!", null);
    }

    @Transactional()
    public ApiResponse<List<YeuCauXoaKhoaHocDto>> getAllYeuCau(TrangThaiYeuCau trangThai) {
        List<YeuCauXoaKhoaHoc> list;
        if (trangThai != null) {
            list = yeuCauXoaKhoaHocRepository.findAllByTrangThai(trangThai);
        } else {
            list = yeuCauXoaKhoaHocRepository.findAll(Sort.by(Sort.Direction.DESC, "ngayTao"));
        }
        List<YeuCauXoaKhoaHocDto> dtos = list.stream().map(this::toDto).collect(Collectors.toList());
        return buildResponse(true, HttpStatus.OK, "OK", dtos);
    }

    private YeuCauXoaKhoaHocDto toDto(YeuCauXoaKhoaHoc y) {
        YeuCauXoaKhoaHocDto dto = new YeuCauXoaKhoaHocDto();
        dto.setMaYeuCau(y.getMaYeuCau());
        dto.setMaKhoaHoc(y.getKhoaHoc().getMaKhoaHoc());
        dto.setTenKhoaHoc(y.getKhoaHoc().getTieuDe());
        dto.setMaGiangVien(y.getGiangVien().getMaNguoiDung());
        dto.setTenGiangVien(y.getGiangVien().getTenNguoiDung());
        dto.setLyDo(y.getLyDo());
        dto.setLyDoTuChoi(y.getLyDoTuChoi());
        dto.setTrangThai(y.getTrangThai());
        dto.setNgayTao(y.getNgayTao());
        return dto;
    }

    private <T> ApiResponse<T> buildResponse(boolean success, HttpStatus status, String message, T data) {
        ApiResponse<T> res = new ApiResponse<>();
        res.setSuccess(success);
        res.setStatusCode(status.value());
        res.setMessage(message);
        res.setData(data);
        return res;
    }
}
