package com.learnspherel.service;

import com.learnspherel.dto.ApiResponse;
import com.learnspherel.dto.GioHangDto;
import com.learnspherel.entity.DangKyKhoaHoc;
import com.learnspherel.entity.GioHang;
import com.learnspherel.entity.KhoaHoc;
import com.learnspherel.entity.NguoiDung;
import com.learnspherel.entity.enums.TrangThaiThanhToan;
import com.learnspherel.exception.GioHangNotFoundException;
import com.learnspherel.exception.KhoaHocNotFoundException;
import com.learnspherel.mapper.GioHangMapper;
import com.learnspherel.repository.DangKyKhoaHocRepository;
import com.learnspherel.repository.GioHangRepository;
import com.learnspherel.repository.KhoaHocRepository;
import com.learnspherel.repository.NguoiDungRepository;
import com.learnspherel.utils.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GioHangService {

    private final GioHangRepository gioHangRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final KhoaHocRepository khoaHocRepository;
    private final DangKyKhoaHocRepository dangKyKhoaHocRepository;
    private final GioHangMapper gioHangMapper;
    private final JwtUtil jwtUtil;
    private final MessageSource messageSource;

    public GioHangService(GioHangRepository gioHangRepository,
                          NguoiDungRepository nguoiDungRepository,
                          KhoaHocRepository khoaHocRepository,
                          DangKyKhoaHocRepository dangKyKhoaHocRepository,
                          GioHangMapper gioHangMapper,
                          JwtUtil jwtUtil,
                          MessageSource messageSource) {
        this.gioHangRepository = gioHangRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.khoaHocRepository = khoaHocRepository;
        this.dangKyKhoaHocRepository = dangKyKhoaHocRepository;
        this.gioHangMapper = gioHangMapper;
        this.jwtUtil = jwtUtil;
        this.messageSource = messageSource;
    }

    public ApiResponse<GioHangDto> addToCart(GioHangDto request, String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            NguoiDung user = nguoiDungRepository.findByUsernameOrEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User không tồn tại"));

            KhoaHoc khoaHoc = khoaHocRepository.findById(request.getMaKhoaHoc())
                    .orElseThrow(() -> new KhoaHocNotFoundException("Khóa học không tồn tại"));

            // Kiểm tra xem khóa học đã có trong giỏ hàng chưa
            if (gioHangRepository.existsByNguoiDungMaNguoiDungAndKhoaHocMaKhoaHoc(user.getMaNguoiDung(), request.getMaKhoaHoc())) {
                return buildResponse(false, HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("gio_hang.already_added", null,
                                "Khóa học đã có trong giỏ hàng", null), null);
            }

            // Kiểm tra xem người dùng đã đăng ký khóa học chưa
            List<DangKyKhoaHoc> dangKyList = dangKyKhoaHocRepository.findByNguoiDungMaNguoiDungAndKhoaHocMaKhoaHoc(user.getMaNguoiDung(), request.getMaKhoaHoc());
            for (DangKyKhoaHoc dangKy : dangKyList) {
                if (dangKy.getTrangThaiThanhToan() == TrangThaiThanhToan.HOAN_THANH) {
                    return buildResponse(false, HttpStatus.BAD_REQUEST,
                            messageSource.getMessage("gio_hang.already_registered", null,
                                    "Bạn đã đăng ký khóa học này", null), null);
                } else if (dangKy.getTrangThaiThanhToan() == TrangThaiThanhToan.DANG_XU_LY) {
                    return buildResponse(false, HttpStatus.BAD_REQUEST,
                            messageSource.getMessage("gio_hang.payment_pending", null,
                                    "Khóa học này đang trong quá trình thanh toán", null), null);
                }
            }

            GioHang gioHang = gioHangMapper.toEntity(request);
            gioHang.setNguoiDung(user);
            gioHang.setKhoaHoc(khoaHoc);
            gioHangRepository.save(gioHang);
            GioHangDto responseDto = gioHangMapper.toDto(gioHang);
            // Lấy trạng thái thanh toán cho response
            List<DangKyKhoaHoc> dangKyResponse = dangKyKhoaHocRepository.findByNguoiDungMaNguoiDungAndKhoaHocMaKhoaHoc(user.getMaNguoiDung(), request.getMaKhoaHoc());
            if (!dangKyResponse.isEmpty()) {
                responseDto.setTrangThaiThanhToan(dangKyResponse.get(0).getTrangThaiThanhToan());
            }
            return buildResponse(true, HttpStatus.CREATED,
                    messageSource.getMessage("gio_hang.add_success", null,
                            "Thêm vào giỏ hàng thành công", null), responseDto);
        } catch (KhoaHocNotFoundException e) {
            return buildResponse(false, HttpStatus.NOT_FOUND,
                    messageSource.getMessage("khoahoc.notfound", null,
                            "Không tìm thấy khóa học", null), null);
        } catch (Exception e) {
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("gio_hang.add_failure", null,
                            "Thêm vào giỏ hàng thất bại", null), null);
        }
    }

    public ApiResponse<List<GioHangDto>> getCartByUser(String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            NguoiDung user = nguoiDungRepository.findByUsernameOrEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User không tồn tại"));

            List<GioHang> gioHangs = gioHangRepository.findByNguoiDungMaNguoiDung(user.getMaNguoiDung());
            List<GioHangDto> responseDtos = gioHangs.stream()
                    .map(gioHang -> {
                        GioHangDto dto = gioHangMapper.toDto(gioHang);
                        // Lấy trạng thái thanh toán từ DangKyKhoaHoc
                        List<DangKyKhoaHoc> dangKyList = dangKyKhoaHocRepository.findByNguoiDungMaNguoiDungAndKhoaHocMaKhoaHoc(
                                user.getMaNguoiDung(), gioHang.getKhoaHoc().getMaKhoaHoc());
                        if (!dangKyList.isEmpty()) {
                            dto.setTrangThaiThanhToan(dangKyList.get(0).getTrangThaiThanhToan());
                        }
                        return dto;
                    })
                    .collect(Collectors.toList());
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("gio_hang.getall.success", null,
                            "Lấy danh sách giỏ hàng thành công", null), responseDtos);
        } catch (UsernameNotFoundException e) {
            return buildResponse(false, HttpStatus.NOT_FOUND,
                    messageSource.getMessage("auth.login.username.notfound", null,
                            "Username not found", null), null);
        } catch (Exception e) {
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("gio_hang.getall.failure", null,
                            "Lấy danh sách giỏ hàng thất bại", null), null);
        }
    }

    public ApiResponse<GioHangDto> getCartItem(Long id) {
        try {
            GioHang gioHang = gioHangRepository.findById(id)
                    .orElseThrow(() -> new GioHangNotFoundException("Mục giỏ hàng không tồn tại"));
            GioHangDto responseDto = gioHangMapper.toDto(gioHang);
            // Lấy trạng thái thanh toán từ DangKyKhoaHoc
            List<DangKyKhoaHoc> dangKyList = dangKyKhoaHocRepository.findByNguoiDungMaNguoiDungAndKhoaHocMaKhoaHoc(
                    gioHang.getNguoiDung().getMaNguoiDung(), gioHang.getKhoaHoc().getMaKhoaHoc());
            if (!dangKyList.isEmpty()) {
                responseDto.setTrangThaiThanhToan(dangKyList.get(0).getTrangThaiThanhToan());
            }
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("gio_hang.get.success", null,
                            "Lấy mục giỏ hàng thành công", null), responseDto);
        } catch (GioHangNotFoundException e) {
            return buildResponse(false, HttpStatus.NOT_FOUND,
                    messageSource.getMessage("gio_hang.notfound", null,
                            "Không tìm thấy mục giỏ hàng", null), null);
        } catch (Exception e) {
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("gio_hang.get.failure", null,
                            "Lấy mục giỏ hàng thất bại", null), null);
        }
    }

    public ApiResponse<Void> deleteCartItem(Long id, String token) {
        try {
            if (jwtUtil.isTokenExpired(token)) {
                return buildResponse(false, HttpStatus.UNAUTHORIZED,
                        messageSource.getMessage("auth.jwt.expired", null, "Jwt token đã hết hạn", null), null);
            }
            String username = jwtUtil.extractUsername(token);
            NguoiDung user = nguoiDungRepository.findByUsernameOrEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User không tồn tại"));

            GioHang gioHang = gioHangRepository.findById(id)
                    .orElseThrow(() -> new GioHangNotFoundException("Mục giỏ hàng không tồn tại"));

            // Kiểm tra quyền sở hữu giỏ hàng
            if (!gioHang.getNguoiDung().getMaNguoiDung().equals(user.getMaNguoiDung())) {
                return buildResponse(false, HttpStatus.FORBIDDEN,
                        messageSource.getMessage("gio_hang.delete.unauthorized", null,
                                "Không có quyền xóa mục giỏ hàng", null), null);
            }

            gioHangRepository.delete(gioHang);
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("gio_hang.delete.success", null,
                            "Xóa mục giỏ hàng thành công", null), null);
        } catch (GioHangNotFoundException e) {
            return buildResponse(false, HttpStatus.NOT_FOUND,
                    messageSource.getMessage("gio_hang.notfound", null,
                            "Không tìm thấy mục giỏ hàng", null), null);
        } catch (Exception e) {
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("gio_hang.delete.failure", null,
                            "Xóa mục giỏ hàng thất bại", null), null);
        }
    }

    public ApiResponse<Void> clearCart(String token) {
        try {
            if (jwtUtil.isTokenExpired(token)) {
                return buildResponse(false, HttpStatus.UNAUTHORIZED,
                        messageSource.getMessage("auth.jwt.expired", null, "Jwt token đã hết hạn", null), null);
            }
            String username = jwtUtil.extractUsername(token);
            NguoiDung user = nguoiDungRepository.findByUsernameOrEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User không tồn tại"));

            gioHangRepository.deleteByNguoiDungMaNguoiDung(user.getMaNguoiDung());
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("gio_hang.clear.success", null,
                            "Xóa toàn bộ giỏ hàng thành công", null), null);
        } catch (UsernameNotFoundException e) {
            return buildResponse(false, HttpStatus.NOT_FOUND,
                    messageSource.getMessage("auth.login.username.notfound", null,
                            "Username not found", null), null);
        } catch (Exception e) {
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("gio_hang.clear.failure", null,
                            "Xóa toàn bộ giỏ hàng thất bại", null), null);
        }
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