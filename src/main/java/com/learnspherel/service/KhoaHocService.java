package com.learnspherel.service;

import com.learnspherel.config.FileStorageProperties;
import com.learnspherel.dto.*;
import com.learnspherel.entity.*;
import com.learnspherel.entity.enums.TrangThaiHocTap;
import com.learnspherel.entity.enums.TrangThaiKhoaHoc;
import com.learnspherel.entity.enums.TrangThaiThanhToan;
import com.learnspherel.entity.enums.TrangThaiYeuCau;
import com.learnspherel.exception.GiangVienNotFoundException;
import com.learnspherel.exception.KhoaHocNotFoundException;
import com.learnspherel.mapper.KhoaHocMapper;
import com.learnspherel.repository.*;
import com.learnspherel.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class KhoaHocService {

    private static final Logger logger = LoggerFactory.getLogger(KhoaHocService.class);

    private final KhoaHocRepository khoaHocRepository;
    private final KyNangRepository kyNangRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final DanhGiaKhoaHocRepository danhGiaKhoaHocRepository;
    private final DanhGiaGiangVienRepository danhGiaGiangVienRepository;
    private final GioHangRepository gioHangRepository;
    private final DangKyKhoaHocRepository dangKyKhoaHocRepository;
    private final TienDoHocTapRepository tienDoHocTapRepository;
    private final BaiHocRepository baiHocRepository;
    private final KhoaHocMapper khoaHocMapper;
    private final MessageSource messageSource;
    private final JwtUtil jwtUtil;
    private final YeuCauXoaKhoaHocRepository yeuCauXoaKhoaHocRepository;
    private final FileStorageProperties fileStorageProperties;
    @Value("${learnspherel.upload.dir}")
    private String uploadDirBase;

    public KhoaHocService(KhoaHocRepository khoaHocRepository,
                          KyNangRepository kyNangRepository,
                          NguoiDungRepository nguoiDungRepository,
                          DanhGiaKhoaHocRepository danhGiaKhoaHocRepository,
                          DanhGiaGiangVienRepository danhGiaGiangVienRepository,
                          GioHangRepository gioHangRepository,
                          DangKyKhoaHocRepository dangKyKhoaHocRepository,
                          TienDoHocTapRepository tienDoHocTapRepository,
                          BaiHocRepository baiHocRepository,
                          KhoaHocMapper khoaHocMapper,
                          MessageSource messageSource,
                          JwtUtil jwtUtil,
                          FileStorageProperties fileStorageProperties,
                          YeuCauXoaKhoaHocRepository yeuCauXoaKhoaHocRepository) {
        this.khoaHocRepository = khoaHocRepository;
        this.kyNangRepository = kyNangRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.danhGiaKhoaHocRepository = danhGiaKhoaHocRepository;
        this.danhGiaGiangVienRepository = danhGiaGiangVienRepository;
        this.gioHangRepository = gioHangRepository;
        this.dangKyKhoaHocRepository = dangKyKhoaHocRepository;
        this.tienDoHocTapRepository = tienDoHocTapRepository;
        this.baiHocRepository = baiHocRepository;
        this.khoaHocMapper = khoaHocMapper;
        this.messageSource = messageSource;
        this.jwtUtil = jwtUtil;
        this.fileStorageProperties = fileStorageProperties;
        this.yeuCauXoaKhoaHocRepository = yeuCauXoaKhoaHocRepository;
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<KhoaHocResponse>> getAllKhoaHoc() {
        try {
            logger.info("Bắt đầu lấy danh sách khóa học");
            List<KhoaHocResponse> responses = khoaHocRepository.findAllByTrangThai(TrangThaiKhoaHoc.DANG_MO).stream()
                    .map(this::mapToResponseWithDetails)
                    .collect(Collectors.toList());
            return buildResponse(true, HttpStatus.OK, messageSource.getMessage("khoahoc.getall.success", null, "Lấy danh sách khóa học thành công", null), responses);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách khóa học: {}", e.getMessage(), e);
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR, messageSource.getMessage("khoahoc.getall.failure", null, "Lấy danh sách khóa học thất bại", null), null);
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<KhoaHocResponse> getKhoaHocById(Long id) {
        try {
            logger.info("Bắt đầu lấy khóa học ID {}", id);
            KhoaHoc khoaHoc = khoaHocRepository.findById(id)
                    .filter(k -> k.getTrangThai() == TrangThaiKhoaHoc.DANG_MO || k.getTrangThai() == TrangThaiKhoaHoc.DANG_DUYET)
                    .orElseThrow(() -> new KhoaHocNotFoundException(messageSource.getMessage("khoahoc.notfound", null, "Không tìm thấy khoá học", null)));
            KhoaHocResponse response = mapToResponseWithDetails(khoaHoc);
            return buildResponse(true, HttpStatus.OK, messageSource.getMessage("khoahoc.get.success", null, "Lấy thông tin khóa học thành công", null), response);
        } catch (KhoaHocNotFoundException e) {
            logger.error("Không tìm thấy khóa học ID {}: {}", id, e.getMessage());
            return buildResponse(false, HttpStatus.NOT_FOUND, e.getMessage(), null);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy khóa học ID {}: {}", id, e.getMessage(), e);
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR, messageSource.getMessage("khoahoc.get.failure", null, "Lấy khóa học thất bại", null), null);
        }
    }
    @Transactional
    public ApiResponse<KhoaHocResponse> requestApproval(Long id) {
        KhoaHoc khoaHoc = khoaHocRepository.findById(id)
                .orElseThrow(() -> new KhoaHocNotFoundException(messageSource.getMessage("khoahoc.notfound", null, "Course not found", null)));
        if (!khoaHoc.getTrangThai().equals(TrangThaiKhoaHoc.DA_DONG)) {
            return buildResponse(false, HttpStatus.BAD_REQUEST, "Chỉ có thể yêu cầu duyệt với khoá học đã đóng.", null);
        }
        khoaHoc.setTrangThai(TrangThaiKhoaHoc.DANG_DUYET);
        KhoaHoc khoaHocSaved = khoaHocRepository.save(khoaHoc);
        return buildResponse(true, HttpStatus.OK, "Đã gửi yêu cầu duyệt.", mapToResponseWithDetails(khoaHocSaved));
    }
    public ApiResponse<List<KhoaHocResponse>> getPendingApprovalCourses() {
        List<KhoaHoc> list = khoaHocRepository.findByTrangThai(TrangThaiKhoaHoc.DANG_DUYET);
        List<KhoaHocResponse> data = list.stream().map(this::mapToResponseWithDetails).collect(Collectors.toList());
        return buildResponse(true, HttpStatus.OK, "Danh sách khoá học đang chờ duyệt.", data);
    }

    @Transactional
    public ApiResponse<KhoaHocResponse> createKhoaHoc(KhoaHocRequest request) {
        try {
            logger.info("Bắt đầu tạo khóa học: {}", request.getTieuDe());
            if (request.getMaGiangVien() == null) {
                logger.warn("maGiangVien không được để trống");
                return buildResponse(false, HttpStatus.BAD_REQUEST, messageSource.getMessage("khoahoc.create.giangvien.invalid", null, "Mã giảng viên không được để trống", null), null);
            }
            NguoiDung giangVien = nguoiDungRepository.findById(request.getMaGiangVien())
                    .orElseThrow(() -> new GiangVienNotFoundException(messageSource.getMessage("khoahoc.create.giangvien.invalid", null, "Invalid instructor", null)));

            KhoaHoc khoaHoc = khoaHocMapper.toEntity(request);
            khoaHoc.setGiangVien(giangVien);
            khoaHoc.setNgonNgu(request.getNgonNgu());
            khoaHoc.setThoiHan(request.getThoiHan());
            khoaHoc.setChungChi(request.getChungChi());
            khoaHoc.setTrangThai(TrangThaiKhoaHoc.DA_DONG);
            khoaHoc.setVideoDemoUrl(request.getVideoDemoUrl());
            khoaHoc = khoaHocRepository.save(khoaHoc);
            KhoaHocResponse response = mapToResponseWithDetails(khoaHoc);
            logger.info("Tạo khóa học thành công: ID {}", khoaHoc.getMaKhoaHoc());
            return buildResponse(true, HttpStatus.CREATED, messageSource.getMessage("khoahoc.create.success", null, "Tạo khóa học thành công", null), response);
        } catch (GiangVienNotFoundException e) {
            logger.error("Không tìm thấy giảng viên: {}", e.getMessage());
            return buildResponse(false, HttpStatus.NOT_FOUND, e.getMessage(), null);
        } catch (Exception e) {
            logger.error("Lỗi khi tạo khóa học: {}", e.getMessage(), e);
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR, messageSource.getMessage("khoahoc.create.failure", null, "Tạo khóa học thất bại", null), null);
        }
    }

    @Transactional
    public ApiResponse<KhoaHocResponse> updateKhoaHoc(Long id, KhoaHocRequest request) {
        try {
            logger.info("Bắt đầu cập nhật khóa học ID {}: {}", id, request.getTieuDe());
            if (request.getMaGiangVien() == null) {
                logger.warn("maGiangVien không được để trống");
                return buildResponse(false, HttpStatus.BAD_REQUEST, messageSource.getMessage("khoahoc.update.giangvien.invalid", null, "Mã giảng viên không được để trống", null), null);
            }
            KhoaHoc khoaHoc = khoaHocRepository.findById(id)
                    .filter(k -> k.getTrangThai() == TrangThaiKhoaHoc.DANG_MO)
                    .orElseThrow(() -> new KhoaHocNotFoundException(messageSource.getMessage("khoahoc.notfound", null, "Course not found", null)));

            NguoiDung giangVien = nguoiDungRepository.findById(request.getMaGiangVien())
                    .orElseThrow(() -> new GiangVienNotFoundException(messageSource.getMessage("khoahoc.update.giangvien.invalid", null, "Invalid instructor", null)));

            khoaHoc.setTieuDe(request.getTieuDe());
            khoaHoc.setMoTa(request.getMoTa());
            if (request.getAnhDaiDien() != null && !request.getAnhDaiDien().isBlank()) {
                khoaHoc.setAnhDaiDien(request.getAnhDaiDien());
            }
            if (request.getVideoDemoUrl() != null && !request.getVideoDemoUrl().isBlank()) {
                khoaHoc.setVideoDemoUrl(request.getVideoDemoUrl());
            }
            khoaHoc.setTrinhDo(request.getTrinhDo());
            khoaHoc.setGiangVien(giangVien);
            khoaHoc.setCoPhi(request.getCoPhi());
            khoaHoc.setGia(request.getGia());
            khoaHoc.setNgonNgu(request.getNgonNgu());
            khoaHoc.setThoiHan(request.getThoiHan());
            khoaHoc.setChungChi(request.getChungChi());

            khoaHoc = khoaHocRepository.save(khoaHoc);
            KhoaHocResponse response = mapToResponseWithDetails(khoaHoc);
            logger.info("Cập nhật khóa học thành công: ID {}", id);
            return buildResponse(true, HttpStatus.OK, messageSource.getMessage("khoahoc.update.success", null, "Cập nhật khóa học thành công", null), response);
        } catch (KhoaHocNotFoundException | GiangVienNotFoundException e) {
            logger.error("Không tìm thấy khóa học hoặc giảng viên: {}", e.getMessage());
            return buildResponse(false, HttpStatus.NOT_FOUND, e.getMessage(), null);
        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật khóa học ID {}: {}", id, e.getMessage(), e);
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR, messageSource.getMessage("khoahoc.update.failure", null, "Cập nhật khóa học thất bại", null), null);
        }
    }

    @Transactional
    public ApiResponse<Void> deleteKhoaHoc(Long id) {
        try {
            logger.info("Bắt đầu xóa khóa học ID {}", id);
            KhoaHoc khoaHoc = khoaHocRepository.findById(id)
                    .filter(k -> k.getTrangThai() == TrangThaiKhoaHoc.DANG_MO)
                    .orElseThrow(() -> new KhoaHocNotFoundException(messageSource.getMessage("khoahoc.notfound", null, "Course not found", null)));
            khoaHoc.setTrangThai(TrangThaiKhoaHoc.DA_XOA);
            khoaHocRepository.save(khoaHoc);
            logger.info("Xóa khóa học thành công: ID {}", id);
            return buildResponse(true, HttpStatus.OK, messageSource.getMessage("khoahoc.delete.success", null, "Xóa khóa học thành công", null), null);
        } catch (KhoaHocNotFoundException e) {
            logger.error("Không tìm thấy khóa học ID {}: {}", id, e.getMessage());
            return buildResponse(false, HttpStatus.NOT_FOUND, e.getMessage(), null);
        } catch (Exception e) {
            logger.error("Lỗi khi xóa khóa học ID {}: {}", id, e.getMessage(), e);
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR, messageSource.getMessage("khoahoc.delete.failure", null, "Xóa khóa học thất bại", null), null);
        }
    }

//    @Transactional
//    public ApiResponse<Void> addKyNangToKhoaHoc(Long khoaHocId, Long kyNangId) {
//        try {
//            logger.info("Bắt đầu thêm kỹ năng ID {} vào khóa học ID {}", kyNangId, khoaHocId);
//            KhoaHoc khoaHoc = khoaHocRepository.findById(khoaHocId)
//                    .orElseThrow(() -> new KhoaHocNotFoundException(messageSource.getMessage("khoahoc.notfound", null, "Course not found", null)));
//            KyNang kyNang = kyNangRepository.findById(kyNangId)
//                    .orElseThrow(() -> new KyNangNotFoundException(messageSource.getMessage("kynang.notfound", null, "Skill not found", null)));
//
//            if (!khoaHoc.getKyNangs().contains(kyNang)) {
//                khoaHoc.getKyNangs().add(kyNang);
//                khoaHocRepository.save(khoaHoc);
//            }
//            logger.info("Thêm kỹ năng thành công: khóa học ID {}, kỹ năng ID {}", khoaHocId, kyNangId);
//            return buildResponse(true, HttpStatus.OK, messageSource.getMessage("khoahoc.add_kynang.success", null, "Thêm kỹ năng thành công", null), null);
//        } catch (KhoaHocNotFoundException | KyNangNotFoundException e) {
//            logger.error("Lỗi khi thêm kỹ năng: {}", e.getMessage());
//            return buildResponse(false, HttpStatus.NOT_FOUND, e.getMessage(), null);
//        } catch (Exception e) {
//            logger.error("Lỗi khi thêm kỹ năng vào khóa học ID {}: {}", khoaHocId, e.getMessage(), e);
//            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR, messageSource.getMessage("khoahoc.add_kynang.failure", null, "Thêm kỹ năng thất bại", null), null);
//        }
//    }

    @Transactional
    public ApiResponse<Void> themVaoGioHang(Long maKhoaHoc, String token) {
        try {
            logger.info("Bắt đầu thêm khóa học ID {} vào giỏ hàng", maKhoaHoc);
            String username = jwtUtil.extractUsername(token);
            NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhapOrEmail(username, username)
                    .orElseThrow(() -> new UsernameNotFoundException(messageSource.getMessage("auth.login.username.notfound", null, "Người dùng không tồn tại", null)));

            KhoaHoc khoaHoc = khoaHocRepository.findById(maKhoaHoc)
                    .filter(k -> k.getTrangThai() == TrangThaiKhoaHoc.DANG_MO)
                    .orElseThrow(() -> new KhoaHocNotFoundException(messageSource.getMessage("khoahoc.notfound", null, "Course not found", null)));

            if (gioHangRepository.existsByNguoiDungMaNguoiDungAndKhoaHocMaKhoaHoc(nguoiDung.getMaNguoiDung(), khoaHoc.getMaKhoaHoc())) {
                logger.warn("Khóa học ID {} đã có trong giỏ hàng của người dùng {}", maKhoaHoc, nguoiDung.getMaNguoiDung());
                return buildResponse(false, HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("gio_hang.already_added", null, "Khóa học đã có trong giỏ hàng", null), null);
            }

            GioHang gioHang = GioHang.builder()
                    .nguoiDung(nguoiDung)
                    .khoaHoc(khoaHoc)
                    .build();

            gioHangRepository.save(gioHang);
            logger.info("Thêm khóa học ID {} vào giỏ hàng thành công", maKhoaHoc);
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("gio_hang.add_success", null, "Thêm vào giỏ hàng thành công", null), null);
        } catch (UsernameNotFoundException | KhoaHocNotFoundException e) {
            logger.error("Lỗi khi thêm vào giỏ hàng: {}", e.getMessage());
            return buildResponse(false, HttpStatus.NOT_FOUND, e.getMessage(), null);
        } catch (Exception e) {
            logger.error("Lỗi khi thêm khóa học ID {} vào giỏ hàng: {}", maKhoaHoc, e.getMessage(), e);
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("gio_hang.add_failure", null, "Thêm vào giỏ hàng thất bại", null), null);
        }
    }

    @Transactional
    public ApiResponse<KhoaHocResponse> uploadAnhDaiDien(Long maKhoaHoc, MultipartFile file) {
        try {
            logger.info("Bắt đầu upload ảnh đại diện cho khóa học ID {}", maKhoaHoc);
            KhoaHoc khoaHoc = khoaHocRepository.findById(maKhoaHoc)
                    .filter(k -> k.getTrangThai() == TrangThaiKhoaHoc.DANG_MO)
                    .orElseThrow(() -> new KhoaHocNotFoundException(messageSource.getMessage("khoahoc.notfound", null, "Course not found", null)));

            if (file == null || file.isEmpty()) {
                logger.warn("File ảnh trống cho khóa học ID {}", maKhoaHoc);
                return buildResponse(false, HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("khoahoc.upload_anh.file.empty", null, "File ảnh không được để trống", null), null);
            }

            long maxSizeInBytes = convertToBytes(fileStorageProperties.getMaxFileSize());
            if (file.getSize() > maxSizeInBytes) {
                logger.warn("File ảnh vượt quá kích thước tối đa {}MB cho khóa học ID {}", fileStorageProperties.getMaxFileSize(), maKhoaHoc);
                return buildResponse(false, HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("khoahoc.upload_anh.file.too.large", new Object[]{fileStorageProperties.getMaxFileSize()}, "File ảnh vượt quá kích thước tối đa", null), null);
            }

            String contentType = file.getContentType();
            if (!isValidImageType(contentType)) {
                logger.warn("Định dạng file ảnh không hợp lệ: {} cho khóa học ID {}", contentType, maKhoaHoc);
                return buildResponse(false, HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("khoahoc.upload_anh.file.invalid", null, "Định dạng file ảnh không hợp lệ", null), null);
            }

            String uploadDir = uploadDirBase + "uploads/image/courses/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            if (khoaHoc.getAnhDaiDien() != null && !khoaHoc.getAnhDaiDien().isEmpty()
                    && !khoaHoc.getAnhDaiDien().equalsIgnoreCase("/img/course-default.jpg")) {
                String oldFilePath = uploadDirBase + khoaHoc.getAnhDaiDien().substring(1);
                Files.deleteIfExists(Paths.get(oldFilePath));
            }

            khoaHoc.setAnhDaiDien("/uploads/image/courses/" + fileName);
            khoaHoc = khoaHocRepository.save(khoaHoc);
            KhoaHocResponse response = mapToResponseWithDetails(khoaHoc);
            logger.info("Upload ảnh đại diện thành công cho khóa học ID {}", maKhoaHoc);
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("khoahoc.upload_anh.success", null, "Upload ảnh đại diện thành công", null), response);
        } catch (IOException e) {
            logger.error("Lỗi khi upload ảnh đại diện cho khóa học ID {}: {}", maKhoaHoc, e.getMessage(), e);
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("khoahoc.upload_anh.failure", null, "Upload ảnh đại diện thất bại", null), null);
        } catch (KhoaHocNotFoundException e) {
            logger.error("Không tìm thấy khóa học ID {}: {}", maKhoaHoc, e.getMessage());
            return buildResponse(false, HttpStatus.NOT_FOUND, e.getMessage(), null);
        } catch (Exception e) {
            logger.error("Lỗi không xác định khi upload ảnh đại diện cho khóa học ID {}: {}", maKhoaHoc, e.getMessage(), e);
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("khoahoc.upload_anh.failure", null, "Upload ảnh đại diện thất bại", null), null);
        }
    }

    @Transactional
    public ApiResponse<KhoaHocResponse> uploadVideoDemo(Long maKhoaHoc, MultipartFile file) {
        try {
            logger.info("Bắt đầu upload video demo cho khóa học ID {}", maKhoaHoc);
            KhoaHoc khoaHoc = khoaHocRepository.findById(maKhoaHoc)
                    .filter(k -> k.getTrangThai() == TrangThaiKhoaHoc.DANG_MO)
                    .orElseThrow(() -> new KhoaHocNotFoundException(messageSource.getMessage("khoahoc.notfound", null, "Course not found", null)));

            if (file == null || file.isEmpty()) {
                logger.warn("File video trống cho khóa học ID {}", maKhoaHoc);
                return buildResponse(false, HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("khoahoc.upload_video.file.empty", null, "File video không được để trống", null), null);
            }

            long maxSizeInBytes = convertToBytes(fileStorageProperties.getMaxFileSize());
            if (file.getSize() > maxSizeInBytes) {
                logger.warn("File video vượt quá kích thước tối đa {}MB cho khóa học ID {}", fileStorageProperties.getMaxFileSize(), maKhoaHoc);
                return buildResponse(false, HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("khoahoc.upload_video.file.too.large", new Object[]{fileStorageProperties.getMaxFileSize()}, "File video vượt quá kích thước tối đa", null), null);
            }

            String contentType = file.getContentType();
            if (!isValidVideoType(contentType)) {
                logger.warn("Định dạng file video không hợp lệ: {} cho khóa học ID {}", contentType, maKhoaHoc);
                return buildResponse(false, HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("khoahoc.upload_video.file.invalid", null, "Định dạng file video không hợp lệ", null), null);
            }

            String uploadDir = uploadDirBase + "uploads/video/demo/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            if (khoaHoc.getVideoDemoUrl() != null && !khoaHoc.getVideoDemoUrl().isEmpty()) {
                String oldFilePath = uploadDirBase + khoaHoc.getVideoDemoUrl().substring(1);
                Files.deleteIfExists(Paths.get(oldFilePath));
            }

            khoaHoc.setVideoDemoUrl("/uploads/video/demo/" + fileName);
            khoaHoc = khoaHocRepository.save(khoaHoc);
            KhoaHocResponse response = mapToResponseWithDetails(khoaHoc);
            logger.info("Upload video demo thành công cho khóa học ID {}", maKhoaHoc);
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("khoahoc.upload_video.success", null, "Upload video demo thành công", null), response);
        } catch (IOException e) {
            logger.error("Lỗi khi upload video demo cho khóa học ID {}: {}", maKhoaHoc, e.getMessage(), e);
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("khoahoc.upload_video.failure", null, "Upload video demo thất bại", null), null);
        } catch (KhoaHocNotFoundException e) {
            logger.error("Không tìm thấy khóa học ID {}: {}", maKhoaHoc, e.getMessage());
            return buildResponse(false, HttpStatus.NOT_FOUND, e.getMessage(), null);
        } catch (Exception e) {
            logger.error("Lỗi không xác định khi upload video demo cho khóa học ID {}: {}", maKhoaHoc, e.getMessage(), e);
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("khoahoc.upload_video.failure", null, "Upload video demo thất bại", null), null);
        }
    }
    @Transactional
    public ApiResponse<KhoaHocResponse> approveCourse(Long id) {
        KhoaHoc khoaHoc = khoaHocRepository.findById(id)
                .filter(k -> k.getTrangThai() == TrangThaiKhoaHoc.DANG_DUYET)
                .orElseThrow(() -> new KhoaHocNotFoundException(
                        messageSource.getMessage("khoahoc.notfound", null, "Course not found", null)
                ));
        khoaHoc.setTrangThai(TrangThaiKhoaHoc.DANG_MO);
        khoaHoc = khoaHocRepository.save(khoaHoc);

        KhoaHocResponse res = khoaHocMapper.toResponse(khoaHoc);
        return buildResponse(true,HttpStatus.OK,
                messageSource.getMessage("khoahoc.approve.success", null, "Duyệt khoá học thành công!", null), res);
    }

    @Transactional
    public ApiResponse<KhoaHocResponse> rejectCourse(Long id) {
        KhoaHoc khoaHoc = khoaHocRepository.findById(id)
                .filter(k -> k.getTrangThai() == TrangThaiKhoaHoc.DANG_DUYET)
                .orElseThrow(() -> new KhoaHocNotFoundException(
                        messageSource.getMessage("khoahoc.notfound", null, "Course not found", null)
                ));
        khoaHoc.setTrangThai(TrangThaiKhoaHoc.DA_DONG);
        khoaHoc = khoaHocRepository.save(khoaHoc);

        KhoaHocResponse res = khoaHocMapper.toResponse(khoaHoc);
        return buildResponse(true,HttpStatus.OK,
                messageSource.getMessage("khoahoc.reject.success", null, "Từ chối duyệt khoá học thành công!", null), res);

    }
    @Transactional
    public ApiResponse<KhoaHocResponse> uploadMedia(Long maKhoaHoc, MultipartFile thumbnail, MultipartFile videoDemo) {
        try {
            logger.info("Bắt đầu upload media cho khóa học ID {}", maKhoaHoc);
            KhoaHoc khoaHoc = khoaHocRepository.findById(maKhoaHoc)
                    .orElseThrow(() -> new KhoaHocNotFoundException(messageSource.getMessage("khoahoc.notfound", null, "Course not found", null)));

            boolean hasUpdate = false;

            // ---- 1. ẢNH ĐẠI DIỆN ----
            if (thumbnail != null && !thumbnail.isEmpty()) {
                long maxSizeInBytes = convertToBytes(fileStorageProperties.getMaxFileSize());
                if (thumbnail.getSize() > maxSizeInBytes) {
                    logger.warn("File ảnh vượt quá kích thước tối đa {}MB cho khóa học ID {}", fileStorageProperties.getMaxFileSize(), maKhoaHoc);
                    return buildResponse(false, HttpStatus.BAD_REQUEST,
                            messageSource.getMessage("khoahoc.upload_anh.file.too.large", new Object[]{fileStorageProperties.getMaxFileSize()}, "File ảnh vượt quá kích thước tối đa", null), null);
                }
                String contentType = thumbnail.getContentType();
                if (!isValidImageType(contentType)) {
                    logger.warn("Định dạng file ảnh không hợp lệ: {} cho khóa học ID {}", contentType, maKhoaHoc);
                    return buildResponse(false, HttpStatus.BAD_REQUEST,
                            messageSource.getMessage("khoahoc.upload_anh.file.invalid", null, "Định dạng file ảnh không hợp lệ", null), null);
                }
                String uploadDir = uploadDirBase + "uploads/image/courses/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String fileName = UUID.randomUUID() + "_" + thumbnail.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.write(filePath, thumbnail.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                // Xoá file cũ nếu có và không phải default
                if (khoaHoc.getAnhDaiDien() != null && !khoaHoc.getAnhDaiDien().isEmpty()
                        && !khoaHoc.getAnhDaiDien().equalsIgnoreCase("/img/course-default.jpg")) {
                    String oldFilePath = uploadDirBase + khoaHoc.getAnhDaiDien().substring(1);
                    Files.deleteIfExists(Paths.get(oldFilePath));
                }
                khoaHoc.setAnhDaiDien("/uploads/image/courses/" + fileName);
                hasUpdate = true;
                logger.info("Upload ảnh đại diện thành công cho khóa học ID {}", maKhoaHoc);
            }else if(khoaHoc.getAnhDaiDien() == null){
                khoaHoc.setAnhDaiDien("/img/course-default.jpg");
            }

            // ---- 2. VIDEO DEMO ----
            if (videoDemo != null && !videoDemo.isEmpty()) {
                long maxSizeInBytes = convertToBytes(fileStorageProperties.getMaxFileSize());
                if (videoDemo.getSize() > maxSizeInBytes) {
                    logger.warn("File video vượt quá kích thước tối đa {}MB cho khóa học ID {}", fileStorageProperties.getMaxFileSize(), maKhoaHoc);
                    return buildResponse(false, HttpStatus.BAD_REQUEST,
                            messageSource.getMessage("khoahoc.upload_video.file.too.large", new Object[]{fileStorageProperties.getMaxFileSize()}, "File video vượt quá kích thước tối đa", null), null);
                }
                String contentType = videoDemo.getContentType();
                if (!isValidVideoType(contentType)) {
                    logger.warn("Định dạng file video không hợp lệ: {} cho khóa học ID {}", contentType, maKhoaHoc);
                    return buildResponse(false, HttpStatus.BAD_REQUEST,
                            messageSource.getMessage("khoahoc.upload_video.file.invalid", null, "Định dạng file video không hợp lệ", null), null);
                }
                String uploadDir = uploadDirBase + "uploads/video/demo/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String fileName = UUID.randomUUID() + "_" + videoDemo.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.write(filePath, videoDemo.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                // Xoá file video cũ nếu có
                if (khoaHoc.getVideoDemoUrl() != null && !khoaHoc.getVideoDemoUrl().isEmpty()) {
                    String oldFilePath = uploadDirBase + khoaHoc.getVideoDemoUrl().substring(1);
                    Files.deleteIfExists(Paths.get(oldFilePath));
                }
                khoaHoc.setVideoDemoUrl("/uploads/video/demo/" + fileName);
                hasUpdate = true;
                logger.info("Upload video demo thành công cho khóa học ID {}", maKhoaHoc);
            }

            if (hasUpdate) {
                khoaHoc = khoaHocRepository.save(khoaHoc);
            }

            KhoaHocResponse response = mapToResponseWithDetails(khoaHoc);
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("khoahoc.upload_media.success", null, "Upload media thành công", null), response);

        } catch (IOException e) {
            logger.error("Lỗi khi upload media cho khóa học ID {}: {}", maKhoaHoc, e.getMessage(), e);
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("khoahoc.upload_media.failure", null, "Upload media thất bại", null), null);
        } catch (KhoaHocNotFoundException e) {
            logger.error("Không tìm thấy khóa học ID {}: {}", maKhoaHoc, e.getMessage());
            return buildResponse(false, HttpStatus.NOT_FOUND, e.getMessage(), null);
        } catch (Exception e) {
            logger.error("Lỗi không xác định khi upload media cho khóa học ID {}: {}", maKhoaHoc, e.getMessage(), e);
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("khoahoc.upload_media.failure", null, "Upload media thất bại", null), null);
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<String> getVideoDemoUrl(Long id) {
        try {
            logger.info("Bắt đầu lấy URL video demo cho khóa học ID {}", id);
            KhoaHoc khoaHoc = khoaHocRepository.findById(id)
                    .filter(k -> k.getTrangThai() == TrangThaiKhoaHoc.DANG_MO)
                    .orElseThrow(() -> new KhoaHocNotFoundException(messageSource.getMessage("khoahoc.notfound", null, "Course not found", null)));
            String videoDemoUrl = khoaHoc.getVideoDemoUrl();
            logger.info("Lấy URL video demo thành công cho khóa học ID {}", id);
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("khoahoc.get.videodemo.success", null, "Video demo retrieved successfully", null), videoDemoUrl);
        } catch (KhoaHocNotFoundException e) {
            logger.error("Không tìm thấy khóa học ID {}: {}", id, e.getMessage());
            return buildResponse(false, HttpStatus.NOT_FOUND, e.getMessage(), null);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy URL video demo cho khóa học ID {}: {}", id, e.getMessage(), e);
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("khoahoc.get.videodemo.failure", null, "Lấy URL video demo thất bại", null), null);
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<MyCourseResponse>> getMyCourses(String token) {
        try {
            logger.info("Bắt đầu lấy danh sách khóa học của người dùng");
            String username = jwtUtil.extractUsername(token);
            NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhapOrEmail(username, username)
                    .orElseThrow(() -> new UsernameNotFoundException(messageSource.getMessage("auth.login.username.notfound", null, "Người dùng không tồn tại", null)));

            List<DangKyKhoaHoc> dangKyList = dangKyKhoaHocRepository.findByNguoiDungMaNguoiDungAndTrangThaiThanhToan(nguoiDung.getMaNguoiDung(), TrangThaiThanhToan.HOAN_THANH);
            if (dangKyList.isEmpty()) {
                logger.info("Không có khóa học nào cho người dùng {}", nguoiDung.getMaNguoiDung());
                return buildResponse(true, HttpStatus.OK,
                        messageSource.getMessage("khoahoc.my_courses.empty", null, "Không có khóa học nào", null),
                        new ArrayList<>());
            }

            List<MyCourseResponse> responses = dangKyList.stream()
                    .map(dangKy -> {
                        KhoaHoc khoaHoc = dangKy.getKhoaHoc();
                        MyCourseResponse response = new MyCourseResponse();
                        response.setMaKhoaHoc(khoaHoc.getMaKhoaHoc());
                        response.setTieuDe(khoaHoc.getTieuDe());
                        response.setType(khoaHoc.getCoPhi() && khoaHoc.getGia() != null && khoaHoc.getGia() > 0 ? "TRẢ PHÍ" : "MIỄN PHÍ");
                        response.setAnhDaiDien(khoaHoc.getAnhDaiDien());
                        List<TienDoHocTap> tienDoList = tienDoHocTapRepository.findByNguoiDungMaNguoiDungAndKhoaHocMaKhoaHoc(
                                nguoiDung.getMaNguoiDung(), khoaHoc.getMaKhoaHoc());
                        List<BaiHoc> baiHocList = baiHocRepository.findByKhoaHocMaKhoaHoc(khoaHoc.getMaKhoaHoc());
                        int totalLessons = baiHocList.size();
                        int completedLessons = tienDoList.stream()
                                .filter(tienDo -> tienDo.getTrangThai().equals(TrangThaiHocTap.HOAN_THANH))
                                .mapToInt(tienDo -> 1)
                                .sum();
                        double progress = totalLessons > 0 ? (completedLessons * 100.0 / totalLessons) : 0.0;
                        response.setTienDo(progress);

                        List<Object[]> danhGiaList = danhGiaKhoaHocRepository.findAverageAndCountByKhoaHocId(khoaHoc.getMaKhoaHoc());
                        double rating = 0.0;
                        if (!danhGiaList.isEmpty() && danhGiaList.get(0)[0] != null) {
                            rating = ((Number) danhGiaList.get(0)[0]).doubleValue();
                        }
                        response.setRating(rating);

                        double totalDuration = baiHocList.stream()
                                .mapToDouble(baiHoc -> baiHoc.getThoiLuong() != null ? baiHoc.getThoiLuong() : 0.0)
                                .sum();
                        response.setThoiLuong(String.format("%.1f Giờ", totalDuration));

                        String status = totalLessons > 0 && completedLessons == totalLessons ? "completed" : "in-progress";
                        response.setStatus(status);

                        return response;
                    })
                    .collect(Collectors.toList());

            logger.info("Lấy danh sách khóa học của người dùng thành công");
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("khoahoc.my_courses.success", null, "Lấy danh sách khóa học thành công", null),
                    responses);
        } catch (UsernameNotFoundException e) {
            logger.error("Không tìm thấy người dùng: {}", e.getMessage());
            return buildResponse(false, HttpStatus.NOT_FOUND,
                    messageSource.getMessage("auth.login.username.notfound", null, "Người dùng không tồn tại", null),
                    null);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách khóa học của người dùng: {}", e.getMessage(), e);
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("khoahoc.my_courses.failure", null, "Lấy danh sách khóa học thất bại", null),
                    null);
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<KhoaHocResponse>> getCoursesByInstructorToken(String token) {
        try {
            logger.info("Bắt đầu lấy danh sách khóa học của giảng viên");
            String username = jwtUtil.extractUsername(token);
            NguoiDung giangVien = nguoiDungRepository.findByTenDangNhapOrEmail(username, username)
                    .orElseThrow(() -> new UsernameNotFoundException(messageSource.getMessage("auth.login.username.notfound", null, "Người dùng không tồn tại", null)));

            List<KhoaHoc> courses = khoaHocRepository.findAllByTrangThaiNotAndGiangVien_MaNguoiDung(
                    TrangThaiKhoaHoc.DA_XOA,
                    giangVien.getMaNguoiDung()
            );
            if (courses.isEmpty()) {
                logger.info("Không có khóa học nào cho giảng viên {}", giangVien.getMaNguoiDung());
                return buildResponse(true, HttpStatus.OK,
                        messageSource.getMessage("khoahoc.instructor_courses.empty", null, "Không có khóa học nào", null),
                        new ArrayList<>());
            }

            List<KhoaHocResponse> responses = courses.stream()
                    .map(this::mapToResponseWithDetails)
                    .collect(Collectors.toList());

            logger.info("Lấy danh sách khóa học của giảng viên thành công");
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("khoahoc.instructor_courses.success", null, "Lấy danh sách khóa học của giảng viên thành công", null),
                    responses);
        } catch (UsernameNotFoundException e) {
            logger.error("Không tìm thấy giảng viên: {}", e.getMessage());
            return buildResponse(false, HttpStatus.NOT_FOUND,
                    messageSource.getMessage("auth.login.username.notfound", null, "Người dùng không tồn tại", null),
                    null);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách khóa học của giảng viên: {}", e.getMessage(), e);
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("khoahoc.instructor_courses.failure", null, "Lấy danh sách khóa học của giảng viên thất bại", null),
                    null);
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<RevenueByMonthResponse>> getRevenueByMonth(String token, Integer year, Integer month) {
        try {
            logger.info("Bắt đầu lấy doanh thu theo tháng cho giảng viên, year: {}, month: {}", year, month);
            String username = jwtUtil.extractUsername(token);
            NguoiDung giangVien = nguoiDungRepository.findByTenDangNhapOrEmail(username, username)
                    .orElseThrow(() -> new UsernameNotFoundException(messageSource.getMessage("auth.login.username.notfound", null, "Người dùng không tồn tại", null)));

            List<KhoaHoc> courses = khoaHocRepository.findAllByTrangThaiAndGiangVien_MaNguoiDung(TrangThaiKhoaHoc.DANG_MO, giangVien.getMaNguoiDung());
            if (courses.isEmpty()) {
                logger.info("Không có khóa học nào cho giảng viên {}", giangVien.getMaNguoiDung());
                return buildResponse(true, HttpStatus.OK,
                        messageSource.getMessage("revenue.get.empty", null, "Không có dữ liệu doanh thu", null),
                        new ArrayList<>());
            }

            // Lấy danh sách đăng ký hoàn thành
            List<DangKyKhoaHoc> registrations = dangKyKhoaHocRepository.findByKhoaHocInAndTrangThaiThanhToan(courses, TrangThaiThanhToan.HOAN_THANH);
            if (registrations.isEmpty()) {
                logger.info("Không có đăng ký hoàn thành nào cho giảng viên {}", giangVien.getMaNguoiDung());
                return buildResponse(true, HttpStatus.OK,
                        messageSource.getMessage("revenue.get.empty", null, "Không có dữ liệu doanh thu", null),
                        new ArrayList<>());
            }

            // Nhóm theo tháng
            Map<String, List<RevenueByMonthResponse.CourseRevenue>> monthlyRevenue = new HashMap<>();
            registrations.forEach(reg -> {
                LocalDateTime ngayDangKy = reg.getNgayDangKy();
                if (ngayDangKy == null) {
                    logger.warn("Đăng ký khóa học ID {} có ngay_dang_ky null", reg.getMaDangKy());
                    return;
                }
                if (year != null && ngayDangKy.getYear() != year) return;
                if (month != null && ngayDangKy.getMonthValue() != month) return;

                String monthYear = ngayDangKy.format(DateTimeFormatter.ofPattern("MM yyyy"));
                KhoaHoc khoaHoc = reg.getKhoaHoc();
                if (khoaHoc.getCoPhi() && khoaHoc.getGia() != null && khoaHoc.getGia() > 0) {
                    double revenue = khoaHoc.getGia() * 0.7; // 70% doanh thu
                    monthlyRevenue.computeIfAbsent(monthYear, k -> new ArrayList<>())
                            .add(new RevenueByMonthResponse.CourseRevenue(
                                    khoaHoc.getTieuDe(),
                                    1,
                                    revenue,
                                    "70%"
                            ));
                }
            });
            if (monthlyRevenue.isEmpty()) {
                return buildResponse(true, HttpStatus.OK,
                        messageSource.getMessage("revenue.get.failure", null, "Tháng này không có doanh thu", null),
                        new ArrayList<>());
            }
            // Gộp dữ liệu theo khóa học trong cùng tháng
            List<RevenueByMonthResponse> result = monthlyRevenue.entrySet().stream()
                    .map(entry -> {
                        String monthYear = entry.getKey();
                        List<RevenueByMonthResponse.CourseRevenue> courseRevenues = entry.getValue().stream()
                                .collect(Collectors.groupingBy(
                                        RevenueByMonthResponse.CourseRevenue::getTieuDe,
                                        Collectors.reducing(
                                                new RevenueByMonthResponse.CourseRevenue("", 0, 0.0, "70%"),
                                                (r1, r2) -> new RevenueByMonthResponse.CourseRevenue(
                                                        r1.getTieuDe().isEmpty() ? r2.getTieuDe() : r1.getTieuDe(),
                                                        r1.getSoDonHang() + r2.getSoDonHang(),
                                                        r1.getDoanhThu() + r2.getDoanhThu(),
                                                        "70%"
                                                )
                                        )
                                ))
                                .values().stream()
                                .collect(Collectors.toList());
                        double totalRevenue = courseRevenues.stream()
                                .mapToDouble(RevenueByMonthResponse.CourseRevenue::getDoanhThu)
                                .sum();
                        return new RevenueByMonthResponse(monthYear, courseRevenues, totalRevenue);
                    })
                    .sorted((a, b) -> {
                        YearMonth dateA = YearMonth.parse(a.getMonthYear(), DateTimeFormatter.ofPattern("MM yyyy"));
                        YearMonth dateB = YearMonth.parse(b.getMonthYear(), DateTimeFormatter.ofPattern("MM yyyy"));
                        return dateB.compareTo(dateA); // Sắp xếp giảm dần theo thời gian
                    })
                    .collect(Collectors.toList());

            logger.info("Lấy dữ liệu doanh thu theo tháng thành công");
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("revenue.get.success", null, "Lấy dữ liệu doanh thu thành công", null),
                    result);
        } catch (UsernameNotFoundException e) {
            logger.error("Không tìm thấy giảng viên: {}", e.getMessage());
            return buildResponse(false, HttpStatus.NOT_FOUND,
                    messageSource.getMessage("auth.login.username.notfound", null, "Người dùng không tồn tại", null),
                    null);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy dữ liệu doanh thu theo tháng: {}", e.getMessage(), e);
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("revenue.get.failure", null, "Lấy dữ liệu doanh thu thất bại", null),
                    null);
        }
//        catch (Exception e) {
//            logger.error("Lỗi khi lấy dữ liệu doanh thu theo tháng: {}", e.getMessage(), e);
//            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
//                    messageSource.getMessage("revenue.get.failure", null, "Lấy dữ liệu doanh thu thất bại", null),
//                    null);
//        }
    }

    private boolean isValidImageType(String contentType) {
        if (contentType == null) return false;
        return contentType.startsWith("image/") && (
                contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/gif") ||
                        contentType.equals("image/bmp")
        );
    }

    private boolean isValidVideoType(String contentType) {
        if (contentType == null) return false;
        return contentType.startsWith("video/") && (
                contentType.equals("video/mp4") ||
                        contentType.equals("video/mpeg") ||
                        contentType.equals("video/webm")
        );
    }

    private long convertToBytes(String maxFileSize) {
        maxFileSize = maxFileSize.toUpperCase();
        if (maxFileSize.endsWith("MB")) {
            return Long.parseLong(maxFileSize.replace("MB", "").trim()) * 1024 * 1024;
        } else if (maxFileSize.endsWith("KB")) {
            return Long.parseLong(maxFileSize.replace("KB", "").trim()) * 1024;
        } else if (maxFileSize.endsWith("GB")) {
            return Long.parseLong(maxFileSize.replace("GB", "").trim()) * 1024 * 1024 * 1024;
        }
        return Long.parseLong(maxFileSize.trim());
    }

    private KhoaHocResponse mapToResponseWithDetails(KhoaHoc khoaHoc) {
        KhoaHocResponse response = khoaHocMapper.toResponse(khoaHoc);
        List<YeuCauXoaKhoaHoc> yeuCauXoaKhoaHoc = yeuCauXoaKhoaHocRepository.findByGiangVienAndKhoaHocAndTrangThai(khoaHoc.getGiangVien(), khoaHoc, TrangThaiYeuCau.CHO_DUYET);
        response.setCoYeuCauXoaDangCho(yeuCauXoaKhoaHoc != null && !yeuCauXoaKhoaHoc.isEmpty());
        response.setTrangThai(khoaHoc.getTrangThai());
        if (khoaHoc.getBaiHocs() != null) {
            Double tongThoiLuong = khoaHoc.getBaiHocs().stream()
                    .mapToDouble(baiHoc -> baiHoc.getThoiLuong() != null ? baiHoc.getThoiLuong() : 0.0)
                    .sum();
            response.setTongThoiLuong(tongThoiLuong);
            response.setSoBaiGiang(khoaHoc.getBaiHocs().size());
        }
        if (khoaHoc.getDangKyKhoaHocs() != null) {
            response.setSoNguoiThamGia(khoaHoc.getDangKyKhoaHocs()
                    .stream()
                    .filter(dangKyKhoaHoc -> dangKyKhoaHoc.getTrangThaiThanhToan().equals(TrangThaiThanhToan.HOAN_THANH))
                    .mapToInt(tienDo -> 1)
                    .sum());
            long soHocVien = khoaHoc.getGiangVien().getKhoaHocGiangVien().stream()
                    .flatMap(kh -> kh.getDangKyKhoaHocs().stream())
                    .map(DangKyKhoaHoc::getNguoiDung)
                    .distinct()
                    .count();
            response.setSoHocVienGiangVien((int) soHocVien);
        }
        List<Object[]> danhGiaKhoaHoc = danhGiaKhoaHocRepository.findAverageAndCountByKhoaHocId(khoaHoc.getMaKhoaHoc());
        if (!danhGiaKhoaHoc.isEmpty()) {
            Object[] result = danhGiaKhoaHoc.get(0);
            response.setDiemDanhGiaKhoaHoc(result[0] != null ? ((Number) result[0]).doubleValue() : 0.0);
            response.setSoDanhGiaKhoaHoc(((Number) result[1]).intValue());
        } else {
            response.setDiemDanhGiaKhoaHoc(0.0);
            response.setSoDanhGiaKhoaHoc(0);
        }

        List<Object[]> danhGiaGiangVien = danhGiaGiangVienRepository.findAverageAndCountByGiangVienId(khoaHoc.getGiangVien().getMaNguoiDung());
        if (!danhGiaGiangVien.isEmpty()) {
            Object[] result = danhGiaGiangVien.get(0);
            response.setDiemDanhGiaGiangVien(result[0] != null ? ((Number) result[0]).doubleValue() : 0.0);
            response.setSoDanhGiaGiangVien(((Number) result[1]).intValue());
        } else {
            response.setDiemDanhGiaGiangVien(0.0);
            response.setSoDanhGiaGiangVien(0);
        }
        if (khoaHoc.getGiangVien().getKhoaHocGiangVien() != null && khoaHoc.getGiangVien().getKhoaHocGiangVien().isEmpty())
            response.setSoKhoaHocGiangVien(khoaHoc.getGiangVien().getKhoaHocGiangVien().size());
        return response;
    }

    private <T> ApiResponse<T> buildResponse(boolean success, HttpStatus status, String message, T data) {
        return ApiResponse.<T>builder()
                .success(success)
                .statusCode(status.value())
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}