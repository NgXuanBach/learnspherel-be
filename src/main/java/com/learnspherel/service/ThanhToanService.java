package com.learnspherel.service;

import com.learnspherel.dto.ApiResponse;
import com.learnspherel.dto.ThanhToanDto;
import com.learnspherel.entity.DangKyKhoaHoc;
import com.learnspherel.entity.GiaoDich;
import com.learnspherel.entity.KhoaHoc;
import com.learnspherel.entity.NguoiDung;
import com.learnspherel.entity.enums.PhuongThucThanhToan;
import com.learnspherel.entity.enums.TrangThaiGiaoDich;
import com.learnspherel.entity.enums.TrangThaiThanhToan;
import com.learnspherel.exception.ThanhToanNotFoundException;
import com.learnspherel.mapper.ThanhToanMapper;
import com.learnspherel.repository.*;
import com.learnspherel.utils.JwtUtil;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ThanhToanService {

    private final GioHangRepository gioHangRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final DangKyKhoaHocRepository dangKyKhoaHocRepository;
    private final GiaoDichRepository giaoDichRepository;
    private final KhoaHocRepository khoaHocRepository;
    private final ThanhToanMapper thanhToanMapper;
    private final JwtUtil jwtUtil;
    private final MessageSource messageSource;
    private final APIContext apiContext;
    private final String successUrl;
    private final String cancelUrl;

    public ThanhToanService(GioHangRepository gioHangRepository,
                            NguoiDungRepository nguoiDungRepository,
                            DangKyKhoaHocRepository dangKyKhoaHocRepository,
                            GiaoDichRepository giaoDichRepository,
                            KhoaHocRepository khoaHocRepository,
                            ThanhToanMapper thanhToanMapper,
                            JwtUtil jwtUtil,
                            MessageSource messageSource,
                            @Value("${paypal.client.id}") String clientId,
                            @Value("${paypal.client.secret}") String clientSecret,
                            @Value("${paypal.mode}") String mode,
                            @Value("${paypal.success.url}") String successUrl,
                            @Value("${paypal.cancel.url}") String cancelUrl) {
        this.gioHangRepository = gioHangRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.dangKyKhoaHocRepository = dangKyKhoaHocRepository;
        this.giaoDichRepository = giaoDichRepository;
        this.khoaHocRepository = khoaHocRepository;
        this.thanhToanMapper = thanhToanMapper;
        this.jwtUtil = jwtUtil;
        this.messageSource = messageSource;
        this.apiContext = new APIContext(clientId, clientSecret, mode);
        this.successUrl = successUrl;
        this.cancelUrl = cancelUrl;
    }

    public ApiResponse<String> createPayment(String token, List<Long> khoaHocIds) {
        try {
            String username = jwtUtil.extractUsername(token);
            NguoiDung user = nguoiDungRepository.findByUsernameOrEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User không tồn tại"));
            // Lấy danh sách khóa học dựa trên khoaHocIds
            List<KhoaHoc> khoaHocs = khoaHocRepository.findAllById(khoaHocIds);
            if (khoaHocs.isEmpty()) {
                return buildResponse(false, HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("khoa_hoc.notfound", null, "Không tìm thấy khóa học", null), null);
            }

            // Kiểm tra các khóa học không tồn tại trong danh sách yêu cầu
            if (khoaHocs.size() != khoaHocIds.size()) {
                return buildResponse(false, HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("khoa_hoc.invalid", null, "Một hoặc nhiều khóa học không tồn tại", null), null);
            }

            // Tính tổng số tiền và xử lý khóa học
            List<DangKyKhoaHoc> dangKyList = new ArrayList<>();
            List<KhoaHoc> freeCourses = new ArrayList<>();
            double totalAmount = 0.0;

            for (KhoaHoc khoaHoc : khoaHocs) {
                // Kiểm tra các bản ghi đăng ký hiện có
                List<DangKyKhoaHoc> existingDangKy = dangKyKhoaHocRepository.findByNguoiDungMaNguoiDungAndKhoaHocMaKhoaHoc(
                        user.getMaNguoiDung(), khoaHoc.getMaKhoaHoc());
                for (DangKyKhoaHoc dangKy : existingDangKy) {
                    if (dangKy.getTrangThaiThanhToan() == TrangThaiThanhToan.HOAN_THANH) {
                        return buildResponse(false, HttpStatus.BAD_REQUEST,
                                messageSource.getMessage("gio_hang.already_registered", null,
                                        "Bạn đã đăng ký khóa học này", null), null);
                    }
                }

                // Xóa các bản ghi DangKyKhoaHoc và GiaoDich cũ nếu trạng thái là DANG_XU_LY hoặc THAT_BAI
                for (DangKyKhoaHoc dangKy : existingDangKy) {
                    if (dangKy.getTrangThaiThanhToan() == TrangThaiThanhToan.DANG_XU_LY ||
                            dangKy.getTrangThaiThanhToan() == TrangThaiThanhToan.THAT_BAI) {
                        List<GiaoDich> oldGiaoDich = giaoDichRepository.findByDangKyMaDangKy(dangKy.getMaDangKy());
                        giaoDichRepository.deleteAll(oldGiaoDich);
                        dangKyKhoaHocRepository.delete(dangKy);
                    }
                }

                if (!khoaHoc.getCoPhi() || khoaHoc.getGia() == null || khoaHoc.getGia() == 0.0) {
                    // Khóa học miễn phí
                    DangKyKhoaHoc dangKy = DangKyKhoaHoc.builder()
                            .nguoiDung(user)
                            .khoaHoc(khoaHoc)
                            .trangThaiThanhToan(TrangThaiThanhToan.HOAN_THANH)
                            .build();
                    dangKyKhoaHocRepository.save(dangKy);
                    freeCourses.add(khoaHoc);
                } else {
                    totalAmount += khoaHoc.getGia();
                }
            }

            // Xóa các mục trong giỏ hàng tương ứng với khoaHocIds
            gioHangRepository.deleteByNguoiDungMaNguoiDungAndKhoaHocMaKhoaHocIn(user.getMaNguoiDung(), khoaHocIds);

            // Nếu không có khóa học nào cần thanh toán
            if (totalAmount == 0.0 && freeCourses.size() == khoaHocs.size()) {
                return buildResponse(true, HttpStatus.OK,
                        messageSource.getMessage("thanh_toan.free_course.success", null,
                                "Đăng ký khóa học miễn phí thành công", null), null);
            }

            // Tạo bản ghi DangKyKhoaHoc và GiaoDich cho các khóa học có phí
            List<GiaoDich> giaoDichList = new ArrayList<>();
            for (KhoaHoc khoaHoc : khoaHocs) {
                if (!freeCourses.contains(khoaHoc)) {
                    DangKyKhoaHoc dangKy = DangKyKhoaHoc.builder()
                            .nguoiDung(user)
                            .khoaHoc(khoaHoc)
                            .trangThaiThanhToan(TrangThaiThanhToan.DANG_XU_LY)
                            .build();
                    dangKyKhoaHocRepository.save(dangKy);

                    GiaoDich giaoDich = GiaoDich.builder()
                            .dangKy(dangKy)
                            .soTien(khoaHoc.getGia())
                            .phuongThuc(PhuongThucThanhToan.VI_DIEN_TU)
                            .trangThai(TrangThaiGiaoDich.DANG_XU_LY)
                            .build();
                    giaoDichRepository.save(giaoDich);

                    dangKyList.add(dangKy);
                    giaoDichList.add(giaoDich);
                }
            }

            // Tạo thanh toán PayPal
            Amount amount = new Amount();
            amount.setCurrency("USD");
            amount.setTotal(String.format("%.2f", totalAmount));

            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setDescription("Thanh toán khóa học trên LearnSpherel");

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(transaction);

            Payer payer = new Payer();
            payer.setPaymentMethod("paypal");

            Payment payment = new Payment();
            payment.setIntent("sale");
            payment.setPayer(payer);
            payment.setTransactions(transactions);

            RedirectUrls redirectUrls = new RedirectUrls();
            redirectUrls.setReturnUrl(successUrl);
            redirectUrls.setCancelUrl(cancelUrl);
            payment.setRedirectUrls(redirectUrls);

            Payment createdPayment = payment.create(apiContext);
            String approvalUrl = createdPayment.getLinks().stream()
                    .filter(link -> link.getRel().equalsIgnoreCase("approval_url"))
                    .findFirst()
                    .map(Links::getHref)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy URL phê duyệt"));

            // Trích xuất PayPal token từ approval_url
            String paypalToken = extractPaypalToken(approvalUrl);
            // Lưu paymentId và paypalToken vào GiaoDich
            for (GiaoDich giaoDich : giaoDichList) {
                giaoDich.setMaGiaoDichNganHang(createdPayment.getId());
                giaoDich.setPaypalToken(paypalToken);
                giaoDichRepository.save(giaoDich);
            }

            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("thanh_toan.create.success", null, "Tạo thanh toán thành công", null),
                    approvalUrl);
        } catch (PayPalRESTException e) {
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("thanh_toan.create.failure", null, "Tạo thanh toán thất bại", null), null);
        } catch (UsernameNotFoundException e) {
            return buildResponse(false, HttpStatus.NOT_FOUND,
                    messageSource.getMessage("auth.login.username.notfound", null, "Username not found", null), null);
        } catch (Exception e) {
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("thanh_toan.create.failure", null, "Tạo thanh toán thất bại", null), null);
        }
    }

    // Phương thức trích xuất token từ approval_url
    private String extractPaypalToken(String approvalUrl) {
        try {
            URL url = new URL(approvalUrl);
            String query = url.getQuery();
            for (String param : query.split("&")) {
                if (param.startsWith("token=")) {
                    return param.substring("token=".length());
                }
            }
            throw new RuntimeException("Không tìm thấy token trong approval_url");
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi trích xuất token từ approval_url: " + e.getMessage());
        }
    }

    public ApiResponse<ThanhToanDto> executePayment(String paymentId, String payerId) {
        try {
            Payment payment = new Payment();
            payment.setId(paymentId);

            PaymentExecution paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerId);

            Payment executedPayment = payment.execute(apiContext, paymentExecution);

            if (!executedPayment.getState().equals("approved")) {
                return buildResponse(false, HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("thanh_toan.execute.failure", null, "Thanh toán không được phê duyệt", null), null);
            }

            // Cập nhật trạng thái GiaoDich và DangKyKhoaHoc
            List<GiaoDich> giaoDichList = giaoDichRepository.findByMaGiaoDichNganHang(paymentId);
            if (giaoDichList.isEmpty()) {
                throw new ThanhToanNotFoundException("Không tìm thấy giao dịch với paymentId: " + paymentId);
            }

            // Lấy người dùng từ giao dịch đầu tiên
            NguoiDung user = giaoDichList.get(0).getDangKy().getNguoiDung();
            for (GiaoDich giaoDich : giaoDichList) {
                // Kiểm tra quyền sở hữu giao dịch
                if (!giaoDich.getDangKy().getNguoiDung().getMaNguoiDung().equals(user.getMaNguoiDung())) {
                    return buildResponse(false, HttpStatus.FORBIDDEN,
                            messageSource.getMessage("thanh_toan.unauthorized", null,
                                    "Không có quyền xử lý giao dịch này", null), null);
                }

                giaoDich.setTrangThai(TrangThaiGiaoDich.THANH_CONG);
                giaoDichRepository.save(giaoDich);

                DangKyKhoaHoc dangKy = giaoDich.getDangKy();
                dangKy.setTrangThaiThanhToan(TrangThaiThanhToan.HOAN_THANH);
                dangKyKhoaHocRepository.save(dangKy);

                // Xóa mục khỏi giỏ hàng
                gioHangRepository.deleteByNguoiDungMaNguoiDungAndKhoaHocMaKhoaHoc(
                        user.getMaNguoiDung(), dangKy.getKhoaHoc().getMaKhoaHoc());
            }

            ThanhToanDto responseDto = thanhToanMapper.toDto(giaoDichList.get(0));
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("thanh_toan.execute.success", null, "Thanh toán thành công", null), responseDto);
        } catch (PayPalRESTException e) {
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("thanh_toan.execute.failure", null, "Thanh toán thất bại", null), null);
        } catch (ThanhToanNotFoundException e) {
            return buildResponse(false, HttpStatus.NOT_FOUND,
                    messageSource.getMessage("thanh_toan.notfound", null, "Không tìm thấy giao dịch thanh toán", null), null);
        } catch (Exception e) {
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("thanh_toan.execute.failure", null, "Thanh toán thất bại", null), null);
        }
    }

    public ApiResponse<Void> cancelPayment(String paypalToken) {
        try {
            List<GiaoDich> giaoDichList = giaoDichRepository.findByPaypalToken(paypalToken);
            if (giaoDichList.isEmpty()) {
                throw new ThanhToanNotFoundException("Không tìm thấy giao dịch với token PayPal: " + paypalToken);
            }

            for (GiaoDich giaoDich : giaoDichList) {
                giaoDich.setTrangThai(TrangThaiGiaoDich.THAT_BAI);
                giaoDichRepository.save(giaoDich);

                DangKyKhoaHoc dangKy = giaoDich.getDangKy();
                dangKy.setTrangThaiThanhToan(TrangThaiThanhToan.THAT_BAI);
                dangKyKhoaHocRepository.save(dangKy);
            }

            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("thanh_toan.cancel.success", null, "Hủy thanh toán thành công", null), null);
        } catch (ThanhToanNotFoundException e) {
            return buildResponse(false, HttpStatus.NOT_FOUND,
                    messageSource.getMessage("thanh_toan.notfound", null, "Không tìm thấy giao dịch thanh toán", null), null);
        } catch (Exception e) {
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("thanh_toan.cancel.failure", null, "Hủy thanh toán thất bại", null), null);
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