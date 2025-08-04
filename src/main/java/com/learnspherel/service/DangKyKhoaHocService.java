package com.learnspherel.service;

import com.learnspherel.dto.AdminRevenueReportDto;
import com.learnspherel.dto.ApiResponse;
import com.learnspherel.dto.DangKyKhoaHocResponse;
import com.learnspherel.entity.DangKyKhoaHoc;
import com.learnspherel.entity.GiaoDich;
import com.learnspherel.entity.NguoiDung;
import com.learnspherel.mapper.DangKyKhoaHocMapper;
import com.learnspherel.repository.DangKyKhoaHocRepository;
import com.learnspherel.repository.GiaoDichRepository;
import com.learnspherel.repository.NguoiDungRepository;
import com.learnspherel.utils.JwtUtil;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class DangKyKhoaHocService {

    private final NguoiDungRepository nguoiDungRepository;
    private final DangKyKhoaHocRepository dangKyKhoaHocRepository;
    private final GiaoDichRepository giaoDichRepository;
    private final DangKyKhoaHocMapper dangKyKhoaHocMapper;
    private final JwtUtil jwtUtil;
    private final MessageSource messageSource;

    public DangKyKhoaHocService(
            NguoiDungRepository nguoiDungRepository,
            DangKyKhoaHocRepository dangKyKhoaHocRepository,
            GiaoDichRepository giaoDichRepository,
            DangKyKhoaHocMapper dangKyKhoaHocMapper,
            JwtUtil jwtUtil,
            MessageSource messageSource) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.dangKyKhoaHocRepository = dangKyKhoaHocRepository;
        this.giaoDichRepository = giaoDichRepository;
        this.dangKyKhoaHocMapper = dangKyKhoaHocMapper;
        this.jwtUtil = jwtUtil;
        this.messageSource = messageSource;
    }

    public ApiResponse<List<DangKyKhoaHocResponse>> getOrderHistory(String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            NguoiDung user = nguoiDungRepository.findByUsernameOrEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User không tồn tại"));

            // Lấy danh sách đăng ký khóa học của người dùng
            List<DangKyKhoaHoc> dangKyList = dangKyKhoaHocRepository.findByNguoiDungMaNguoiDung(user.getMaNguoiDung());
            if (dangKyList.isEmpty()) {
                return buildResponse(true, HttpStatus.OK,
                        messageSource.getMessage("dang_ky_khoa_hoc.history.empty", null,
                                "Không có lịch sử đặt hàng", null), new ArrayList<>());
            }

            // Ánh xạ sang DangKyKhoaHocResponse
            List<DangKyKhoaHocResponse> history = dangKyList.stream().map(dangKy -> {
                List<GiaoDich> giaoDichList = giaoDichRepository.findByDangKyMaDangKy(dangKy.getMaDangKy());
                GiaoDich giaoDich = giaoDichList.isEmpty() ? null : giaoDichList.get(0);
                return dangKyKhoaHocMapper.toDto(dangKy, giaoDich);
            }).toList();

            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("dang_ky_khoa_hoc.history.success", null,
                            "Lấy lịch sử đặt hàng thành công", null), history);
        } catch (UsernameNotFoundException e) {
            return buildResponse(false, HttpStatus.NOT_FOUND,
                    messageSource.getMessage("auth.login.username.notfound", null,
                            "User không tồn tại", null), null);
        } catch (Exception e) {
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("dang_ky_khoa_hoc.history.failure", null,
                            "Lấy lịch sử đặt hàng thất bại", null), null);
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<AdminRevenueReportDto>> getRevenueReport(Integer month, Integer year) {
        List<AdminRevenueReportDto> list = dangKyKhoaHocRepository.getAdminRevenueReport(month, year);
        return buildResponse(true, HttpStatus.OK,
                messageSource.getMessage("revenue.report.success", null, "Lấy báo cáo doanh thu thành công!", null),
                list);
    }

    @Transactional(readOnly = true)
    public ApiResponse<Double> getTotalRevenue(Integer month, Integer year) {
        List<AdminRevenueReportDto> list = dangKyKhoaHocRepository.getAdminRevenueReport(month, year);
        double total = list.stream()
                .mapToDouble(r -> r.getAdminNhanDuoc() != null ? r.getAdminNhanDuoc() : 0.0)
                .sum();
        return buildResponse(true, HttpStatus.OK,
                messageSource.getMessage("revenue.total.success", null, "Tổng doanh thu admin nhận được!", null),
                total);
    }

    private <T> ApiResponse<T> buildResponse(boolean isSuccess, HttpStatus status, String message, T data) {
        return ApiResponse.<T>builder()
                .success(isSuccess)
                .statusCode(status.value())
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}