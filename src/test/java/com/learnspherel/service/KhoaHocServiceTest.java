package com.learnspherel.service;

import com.learnspherel.config.FileStorageProperties;
import com.learnspherel.dto.*;
import com.learnspherel.entity.*;
import com.learnspherel.entity.enums.TrangThaiHocTap;
import com.learnspherel.entity.enums.TrangThaiThanhToan;
import com.learnspherel.entity.enums.VaiTro;
import com.learnspherel.mapper.KhoaHocMapper;
import com.learnspherel.repository.*;
import com.learnspherel.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class   KhoaHocServiceTest {

    @Mock
    private KhoaHocRepository khoaHocRepository;
    @Mock
    private KyNangRepository kyNangRepository;
    @Mock
    private NguoiDungRepository nguoiDungRepository;
    @Mock
    private DanhGiaKhoaHocRepository danhGiaKhoaHocRepository;
    @Mock
    private DanhGiaGiangVienRepository danhGiaGiangVienRepository;
    @Mock
    private GioHangRepository gioHangRepository;
    @Mock
    private DangKyKhoaHocRepository dangKyKhoaHocRepository;
    @Mock
    private TienDoHocTapRepository tienDoHocTapRepository;
    @Mock
    private BaiHocRepository baiHocRepository;
    @Mock
    private KhoaHocMapper khoaHocMapper;
    @Mock
    private MessageSource messageSource;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private FileStorageProperties fileStorageProperties;
    @Mock
    private MultipartFile file;

    @InjectMocks
    private KhoaHocService khoaHocService;

    private NguoiDung giangVien;
    private KhoaHoc khoaHoc;
    private KhoaHocResponse khoaHocResponse;
    private KhoaHocRequest khoaHocRequest;
    private KyNang kyNang;
    private NguoiDung hocVien;
    private DangKyKhoaHoc dangKyKhoaHoc;
    private BaiHoc baiHoc;
    private TienDoHocTap tienDoHocTap;

    @BeforeEach
    void setUp() {
        giangVien = new NguoiDung();
        giangVien.setMaNguoiDung(1L);
        giangVien.setTenDangNhap("instructor");
        giangVien.setVaiTro(VaiTro.GIANG_VIEN);

        hocVien = new NguoiDung();
        hocVien.setMaNguoiDung(2L);
        hocVien.setTenDangNhap("student");
        hocVien.setVaiTro(VaiTro.HOC_VIEN);

        khoaHoc = new KhoaHoc();
        khoaHoc.setMaKhoaHoc(1L);
        khoaHoc.setTieuDe("Test Course");
        khoaHoc.setGiangVien(giangVien);
        khoaHoc.setGia(100.0);
        khoaHoc.setCoPhi(true);
        khoaHoc.setVideoDemoUrl("/uploads/video/demo/test.mp4");

        khoaHocResponse = new KhoaHocResponse();
        khoaHocResponse.setMaKhoaHoc(1L);
        khoaHocResponse.setTieuDe("Test Course");

        khoaHocRequest = new KhoaHocRequest();
        khoaHocRequest.setTieuDe("Test Course");
        khoaHocRequest.setMaGiangVien(1L);
        khoaHocRequest.setGia(100.0);
        khoaHocRequest.setCoPhi(true);

        kyNang = new KyNang();
        kyNang.setMaKyNang(1L);
        kyNang.setTenKyNang("Java");

        dangKyKhoaHoc = new DangKyKhoaHoc();
        dangKyKhoaHoc.setMaDangKy(1L);
        dangKyKhoaHoc.setKhoaHoc(khoaHoc);
        dangKyKhoaHoc.setNguoiDung(hocVien);
        dangKyKhoaHoc.setNgayDangKy(LocalDateTime.now());
        dangKyKhoaHoc.setTrangThaiThanhToan(TrangThaiThanhToan.HOAN_THANH);

        baiHoc = new BaiHoc();
        baiHoc.setMaBaiHoc(1L);
        baiHoc.setThoiLuong(1.5);

        tienDoHocTap = new TienDoHocTap();
        tienDoHocTap.setMaTienDo(1L);
        tienDoHocTap.setTrangThai(TrangThaiHocTap.HOAN_THANH);

        // Thiết lập stubbing chung cho messageSource
        lenient().when(messageSource.getMessage(eq("khoahoc.getall.success"), any(), anyString(), any())).thenReturn("Lấy danh sách khóa học thành công");
        lenient().when(messageSource.getMessage(eq("khoahoc.getall.failure"), any(), anyString(), any())).thenReturn("Lấy danh sách khóa học thất bại");
        lenient().when(messageSource.getMessage(eq("khoahoc.get.success"), any(), anyString(), any())).thenReturn("Lấy khóa học thành công");
        lenient().when(messageSource.getMessage(eq("khoahoc.get.failure"), any(), anyString(), any())).thenReturn("Lấy khóa học thất bại");
        lenient().when(messageSource.getMessage(eq("khoahoc.notfound"), any(), anyString(), any())).thenReturn("Course not found");
        lenient().when(messageSource.getMessage(eq("khoahoc.create.success"), any(), anyString(), any())).thenReturn("Tạo khóa học thành công");
        lenient().when(messageSource.getMessage(eq("khoahoc.create.failure"), any(), anyString(), any())).thenReturn("Tạo khóa học thất bại");
        lenient().when(messageSource.getMessage(eq("khoahoc.create.giangvien.invalid"), any(), anyString(), any())).thenReturn("Mã giảng viên không được để trống");
        lenient().when(messageSource.getMessage(eq("khoahoc.update.success"), any(), anyString(), any())).thenReturn("Cập nhật khóa học thành công");
        lenient().when(messageSource.getMessage(eq("khoahoc.update.failure"), any(), anyString(), any())).thenReturn("Cập nhật khóa học thất bại");
        lenient().when(messageSource.getMessage(eq("khoahoc.update.giangvien.invalid"), any(), anyString(), any())).thenReturn("Mã giảng viên không được để trống");
        lenient().when(messageSource.getMessage(eq("khoahoc.delete.success"), any(), anyString(), any())).thenReturn("Xóa khóa học thành công");
        lenient().when(messageSource.getMessage(eq("khoahoc.delete.failure"), any(), anyString(), any())).thenReturn("Xóa khóa học thất bại");
        lenient().when(messageSource.getMessage(eq("khoahoc.add_kynang.success"), any(), anyString(), any())).thenReturn("Thêm kỹ năng thành công");
        lenient().when(messageSource.getMessage(eq("khoahoc.add_kynang.failure"), any(), anyString(), any())).thenReturn("Thêm kỹ năng thất bại");
        lenient().when(messageSource.getMessage(eq("kynang.notfound"), any(), anyString(), any())).thenReturn("Skill not found");
        lenient().when(messageSource.getMessage(eq("gio_hang.add_success"), any(), anyString(), any())).thenReturn("Thêm vào giỏ hàng thành công");
        lenient().when(messageSource.getMessage(eq("gio_hang.add_failure"), any(), anyString(), any())).thenReturn("Thêm vào giỏ hàng thất bại");
        lenient().when(messageSource.getMessage(eq("gio_hang.already_added"), any(), anyString(), any())).thenReturn("Khóa học đã có trong giỏ hàng");
        lenient().when(messageSource.getMessage(eq("khoahoc.upload_anh.success"), any(), anyString(), any())).thenReturn("Upload ảnh đại diện thành công");
        lenient().when(messageSource.getMessage(eq("khoahoc.upload_anh.failure"), any(), anyString(), any())).thenReturn("Upload ảnh đại diện thất bại");
        lenient().when(messageSource.getMessage(eq("khoahoc.upload_anh.file.empty"), any(), anyString(), any())).thenReturn("File ảnh không được để trống");
        lenient().when(messageSource.getMessage(eq("khoahoc.upload_anh.file.too.large"), any(), anyString(), any())).thenReturn("File ảnh vượt quá kích thước tối đa");
        lenient().when(messageSource.getMessage(eq("khoahoc.upload_anh.file.invalid"), any(), anyString(), any())).thenReturn("Định dạng file ảnh không hợp lệ");
        lenient().when(messageSource.getMessage(eq("khoahoc.upload_video.success"), any(), anyString(), any())).thenReturn("Upload video demo thành công");
        lenient().when(messageSource.getMessage(eq("khoahoc.upload_video.failure"), any(), anyString(), any())).thenReturn("Upload video demo thất bại");
        lenient().when(messageSource.getMessage(eq("khoahoc.upload_video.file.empty"), any(), anyString(), any())).thenReturn("File video không được để trống");
        lenient().when(messageSource.getMessage(eq("khoahoc.upload_video.file.too.large"), any(), anyString(), any())).thenReturn("File video vượt quá kích thước tối đa");
        lenient().when(messageSource.getMessage(eq("khoahoc.upload_video.file.invalid"), any(), anyString(), any())).thenReturn("Định dạng file video không hợp lệ");
        lenient().when(messageSource.getMessage(eq("khoahoc.get.videodemo.success"), any(), anyString(), any())).thenReturn("Video demo retrieved successfully");
        lenient().when(messageSource.getMessage(eq("khoahoc.get.videodemo.failure"), any(), anyString(), any())).thenReturn("Lấy URL video demo thất bại");
        lenient().when(messageSource.getMessage(eq("khoahoc.my_courses.success"), any(), anyString(), any())).thenReturn("Lấy danh sách khóa học thành công");
        lenient().when(messageSource.getMessage(eq("khoahoc.my_courses.empty"), any(), anyString(), any())).thenReturn("Không có khóa học nào");
        lenient().when(messageSource.getMessage(eq("khoahoc.my_courses.failure"), any(), anyString(), any())).thenReturn("Lấy danh sách khóa học thất bại");
        lenient().when(messageSource.getMessage(eq("revenue.get.success"), any(), anyString(), any())).thenReturn("Lấy dữ liệu doanh thu thành công");
        lenient().when(messageSource.getMessage(eq("revenue.get.empty"), any(), anyString(), any())).thenReturn("Không có dữ liệu doanh thu");
        lenient().when(messageSource.getMessage(eq("revenue.get.failure"), any(), anyString(), any())).thenReturn("Lấy dữ liệu doanh thu thất bại");
        lenient().when(messageSource.getMessage(eq("auth.login.username.notfound"), any(), anyString(), any())).thenReturn("Người dùng không tồn tại");
    }

    @Test
    void testGetAllKhoaHoc_Success() {
        when(khoaHocRepository.findAll()).thenReturn(Arrays.asList(khoaHoc));
        when(khoaHocMapper.toResponse(khoaHoc)).thenReturn(khoaHocResponse);
//        when(danhGiaKhoaHocRepository.findAverageAndCountByKhoaHocId(1L)).thenReturn(Arrays.asList(new Object[]{4.5, 10}));
//        when(danhGiaGiangVienRepository.findAverageAndCountByGiangVienId(1L)).thenReturn(Arrays.asList(new Object[]{4.0, 5}));

        ApiResponse<List<KhoaHocResponse>> response = khoaHocService.getAllKhoaHoc();

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Lấy danh sách khóa học thành công", response.getMessage());
        assertEquals(1, response.getData().size());
        assertEquals(khoaHocResponse, response.getData().get(0));
    }

    @Test
    void testGetKhoaHocById_Success() {
        when(khoaHocRepository.findById(1L)).thenReturn(Optional.of(khoaHoc));
        when(khoaHocMapper.toResponse(khoaHoc)).thenReturn(khoaHocResponse);
//        when(danhGiaKhoaHocRepository.findAverageAndCountByKhoaHocId(1L)).thenReturn(Arrays.asList(new Object[]{4.5, 10}));
//        when(danhGiaGiangVienRepository.findAverageAndCountByGiangVienId(1L)).thenReturn(Arrays.asList(new Object[]{4.0, 5}));

        ApiResponse<KhoaHocResponse> response = khoaHocService.getKhoaHocById(1L);

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Lấy khóa học thành công", response.getMessage());
        assertEquals(khoaHocResponse, response.getData());
    }

//    @Test
//    void testGetKhoaHocById_NotFound() {
//        when(khoaHocRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(KhoaHocNotFoundException.class, () -> khoaHocService.getKhoaHocById(1L));
//    }

    @Test
    void testCreateKhoaHoc_Success() {
        when(nguoiDungRepository.findById(1L)).thenReturn(Optional.of(giangVien));
        when(khoaHocMapper.toEntity(khoaHocRequest)).thenReturn(khoaHoc);
        when(khoaHocRepository.save(any(KhoaHoc.class))).thenReturn(khoaHoc);
        when(khoaHocMapper.toResponse(khoaHoc)).thenReturn(khoaHocResponse);
//        when(danhGiaKhoaHocRepository.findAverageAndCountByKhoaHocId(1L)).thenReturn(Arrays.asList(new Object[]{0.0, 0}));
//        when(danhGiaGiangVienRepository.findAverageAndCountByGiangVienId(1L)).thenReturn(Arrays.asList(new Object[]{0.0, 0}));

        ApiResponse<KhoaHocResponse> response = khoaHocService.createKhoaHoc(khoaHocRequest);

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
        assertEquals("Tạo khóa học thành công", response.getMessage());
        assertEquals(khoaHocResponse, response.getData());
        verify(khoaHocRepository).save(khoaHoc);
    }

//    @Test
//    void testCreateKhoaHoc_GiangVienNotFound() {
//        when(nguoiDungRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(GiangVienNotFoundException.class, () -> khoaHocService.createKhoaHoc(khoaHocRequest));
//        verify(khoaHocRepository, never()).save(any(KhoaHoc.class));
//    }

    @Test
    void testUpdateKhoaHoc_Success() {
        when(khoaHocRepository.findById(1L)).thenReturn(Optional.of(khoaHoc));
        when(nguoiDungRepository.findById(1L)).thenReturn(Optional.of(giangVien));
        when(khoaHocRepository.save(any(KhoaHoc.class))).thenReturn(khoaHoc);
        when(khoaHocMapper.toResponse(khoaHoc)).thenReturn(khoaHocResponse);
//        when(danhGiaKhoaHocRepository.findAverageAndCountByKhoaHocId(1L)).thenReturn(Arrays.asList(new Object[]{0.0, 0}));
//        when(danhGiaGiangVienRepository.findAverageAndCountByGiangVienId(1L)).thenReturn(Arrays.asList(new Object[]{0.0, 0}));

        ApiResponse<KhoaHocResponse> response = khoaHocService.updateKhoaHoc(1L, khoaHocRequest);

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Cập nhật khóa học thành công", response.getMessage());
        assertEquals(khoaHocResponse, response.getData());
        verify(khoaHocRepository).save(khoaHoc);
    }

//    @Test
//    void testUpdateKhoaHoc_NotFound() {
//        when(khoaHocRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(KhoaHocNotFoundException.class, () -> khoaHocService.updateKhoaHoc(1L, khoaHocRequest));
//        verify(khoaHocRepository, never()).save(any(KhoaHoc.class));
//    }

    @Test
    void testDeleteKhoaHoc_Success() {
        when(khoaHocRepository.findById(1L)).thenReturn(Optional.of(khoaHoc));

        ApiResponse<Void> response = khoaHocService.deleteKhoaHoc(1L);

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Xóa khóa học thành công", response.getMessage());
        verify(khoaHocRepository).delete(khoaHoc);
    }

//    @Test
//    void testDeleteKhoaHoc_NotFound() {
//        when(khoaHocRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(KhoaHocNotFoundException.class, () -> khoaHocService.deleteKhoaHoc(1L));
//        verify(khoaHocRepository, never()).delete(any(KhoaHoc.class));
//    }

//    @Test
//    void testAddKyNangToKhoaHoc_Success() {
//        when(khoaHocRepository.findById(1L)).thenReturn(Optional.of(khoaHoc));
//        when(kyNangRepository.findById(1L)).thenReturn(Optional.of(kyNang));
//        when(khoaHocRepository.save(any(KhoaHoc.class))).thenReturn(khoaHoc);
//
//        ApiResponse<Void> response = khoaHocService.addKyNangToKhoaHoc(1L, 1L);
//
//        assertTrue(response.isSuccess());
//        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
//        assertEquals("Thêm kỹ năng thành công", response.getMessage());
//        verify(khoaHocRepository).save(khoaHoc);
//    }

//    @Test
//    void testAddKyNangToKhoaHoc_KyNangNotFound() {
//        when(khoaHocRepository.findById(1L)).thenReturn(Optional.of(khoaHoc));
//        when(kyNangRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(KyNangNotFoundException.class, () -> khoaHocService.addKyNangToKhoaHoc(1L, 1L));
//        verify(khoaHocRepository, never()).save(any(KhoaHoc.class));
//    }

    @Test
    void testThemVaoGioHang_Success() {
        when(jwtUtil.extractUsername("jwt-token")).thenReturn("student");
        when(nguoiDungRepository.findByTenDangNhapOrEmail("student", "student")).thenReturn(Optional.of(hocVien));
        when(khoaHocRepository.findById(1L)).thenReturn(Optional.of(khoaHoc));
        when(gioHangRepository.existsByNguoiDungMaNguoiDungAndKhoaHocMaKhoaHoc(2L, 1L)).thenReturn(false);
        when(gioHangRepository.save(any(GioHang.class))).thenReturn(new GioHang());

        ApiResponse<Void> response = khoaHocService.themVaoGioHang(1L, "jwt-token");

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Thêm vào giỏ hàng thành công", response.getMessage());
        verify(gioHangRepository).save(any(GioHang.class));
    }

    @Test
    void testThemVaoGioHang_AlreadyAdded() {
        when(jwtUtil.extractUsername("jwt-token")).thenReturn("student");
        when(nguoiDungRepository.findByTenDangNhapOrEmail("student", "student")).thenReturn(Optional.of(hocVien));
        when(khoaHocRepository.findById(1L)).thenReturn(Optional.of(khoaHoc));
        when(gioHangRepository.existsByNguoiDungMaNguoiDungAndKhoaHocMaKhoaHoc(2L, 1L)).thenReturn(true);

        ApiResponse<Void> response = khoaHocService.themVaoGioHang(1L, "jwt-token");

        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("Khóa học đã có trong giỏ hàng", response.getMessage());
        verify(gioHangRepository, never()).save(any(GioHang.class));
    }

    @Test
    void testUploadAnhDaiDien_Success() throws IOException {
        when(khoaHocRepository.findById(1L)).thenReturn(Optional.of(khoaHoc));
        when(fileStorageProperties.getMaxFileSize()).thenReturn("5MB");
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(1024L);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getBytes()).thenReturn(new byte[]{});
        when(khoaHocRepository.save(any(KhoaHoc.class))).thenReturn(khoaHoc);
        when(khoaHocMapper.toResponse(khoaHoc)).thenReturn(khoaHocResponse);

        ApiResponse<KhoaHocResponse> response = khoaHocService.uploadAnhDaiDien(1L, file);

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Upload ảnh đại diện thành công", response.getMessage());
        verify(khoaHocRepository).save(khoaHoc);
    }

    @Test
    void testUploadAnhDaiDien_FileEmpty() {
        when(khoaHocRepository.findById(1L)).thenReturn(Optional.of(khoaHoc));
        when(file.isEmpty()).thenReturn(true);

        ApiResponse<KhoaHocResponse> response = khoaHocService.uploadAnhDaiDien(1L, file);

        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("File ảnh không được để trống", response.getMessage());
        verify(khoaHocRepository, never()).save(any(KhoaHoc.class));
    }

    @Test
    void testUploadVideoDemo_Success() throws IOException {
        when(khoaHocRepository.findById(1L)).thenReturn(Optional.of(khoaHoc));
        when(fileStorageProperties.getMaxFileSize()).thenReturn("50MB");
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(1024L);
        when(file.getContentType()).thenReturn("video/mp4");
        when(file.getOriginalFilename()).thenReturn("test.mp4");
        when(file.getBytes()).thenReturn(new byte[]{});
        when(khoaHocRepository.save(any(KhoaHoc.class))).thenReturn(khoaHoc);
        when(khoaHocMapper.toResponse(khoaHoc)).thenReturn(khoaHocResponse);

        ApiResponse<KhoaHocResponse> response = khoaHocService.uploadVideoDemo(1L, file);

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Upload video demo thành công", response.getMessage());
        verify(khoaHocRepository).save(khoaHoc);
    }
//
//    @Test
//    void testUploadVideoDemo_FileInvalid() {
//        when(khoaHocRepository.findById(1L)).thenReturn(Optional.of(khoaHoc));
//        when(file.isEmpty()).thenReturn(false);
//        when(file.getSize()).thenReturn(1024L);
//        when(file.getContentType()).thenReturn("text/plain");
//
//        ApiResponse<KhoaHocResponse> response = khoaHocService.uploadVideoDemo(1L, file);
//
//        assertFalse(response.isSuccess());
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode());
//        assertEquals("Upload video demo thất bại", response.getMessage());
//        verify(khoaHocRepository, never()).save(any(KhoaHoc.class));
//    }

    @Test
    void testGetVideoDemoUrl_Success() {
        when(khoaHocRepository.findById(1L)).thenReturn(Optional.of(khoaHoc));

        ApiResponse<String> response = khoaHocService.getVideoDemoUrl(1L);

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Video demo retrieved successfully", response.getMessage());
        assertEquals("/uploads/video/demo/test.mp4", response.getData());
    }

//    @Test
//    void testGetVideoDemoUrl_NotFound() {
//        when(khoaHocRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(KhoaHocNotFoundException.class, () -> khoaHocService.getVideoDemoUrl(1L));
//    }

    @Test
    void testGetMyCourses_Success() {
        when(jwtUtil.extractUsername("jwt-token")).thenReturn("student");
        when(nguoiDungRepository.findByTenDangNhapOrEmail("student", "student")).thenReturn(Optional.of(hocVien));
        when(dangKyKhoaHocRepository.findByNguoiDungMaNguoiDungAndTrangThaiThanhToan(2L, TrangThaiThanhToan.HOAN_THANH)).thenReturn(Arrays.asList(dangKyKhoaHoc));
        when(tienDoHocTapRepository.findByNguoiDungMaNguoiDungAndKhoaHocMaKhoaHoc(2L, 1L)).thenReturn(Arrays.asList(tienDoHocTap));
        when(baiHocRepository.findByKhoaHocMaKhoaHoc(1L)).thenReturn(Arrays.asList(baiHoc));
//        when(danhGiaKhoaHocRepository.findAverageAndCountByKhoaHocId(1L)).thenReturn(Arrays.asList(new Object[]{4.5, 10}));

        ApiResponse<List<MyCourseResponse>> response = khoaHocService.getMyCourses("jwt-token");

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Lấy danh sách khóa học thành công", response.getMessage());
        assertEquals(1, response.getData().size());
        assertEquals("completed", response.getData().get(0).getStatus());
    }

    @Test
    void testGetMyCourses_Empty() {
        when(jwtUtil.extractUsername("jwt-token")).thenReturn("student");
        when(nguoiDungRepository.findByTenDangNhapOrEmail("student", "student")).thenReturn(Optional.of(hocVien));
        when(dangKyKhoaHocRepository.findByNguoiDungMaNguoiDungAndTrangThaiThanhToan(2L, TrangThaiThanhToan.HOAN_THANH)).thenReturn(new ArrayList<>());

        ApiResponse<List<MyCourseResponse>> response = khoaHocService.getMyCourses("jwt-token");

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Không có khóa học nào", response.getMessage());
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void testGetRevenueByMonth_Success() {
        when(jwtUtil.extractUsername("jwt-token")).thenReturn("instructor");
        when(nguoiDungRepository.findByTenDangNhapOrEmail("instructor", "instructor")).thenReturn(Optional.of(giangVien));
        when(khoaHocRepository.findByGiangVienMaNguoiDung(1L)).thenReturn(Arrays.asList(khoaHoc));
        when(dangKyKhoaHocRepository.findByKhoaHocInAndTrangThaiThanhToan(Arrays.asList(khoaHoc), TrangThaiThanhToan.HOAN_THANH)).thenReturn(Arrays.asList(dangKyKhoaHoc));

        ApiResponse<List<RevenueByMonthResponse>> response = khoaHocService.getRevenueByMonth("jwt-token", null, null);

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Lấy dữ liệu doanh thu thành công", response.getMessage());
        assertEquals(1, response.getData().size());
        assertEquals(70.0, response.getData().get(0).getTotalRevenue());
    }

    @Test
    void testGetRevenueByMonth_Empty() {
        when(jwtUtil.extractUsername("jwt-token")).thenReturn("instructor");
        when(nguoiDungRepository.findByTenDangNhapOrEmail("instructor", "instructor")).thenReturn(Optional.of(giangVien));
        when(khoaHocRepository.findByGiangVienMaNguoiDung(1L)).thenReturn(new ArrayList<>());

        ApiResponse<List<RevenueByMonthResponse>> response = khoaHocService.getRevenueByMonth("jwt-token", null, null);

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Không có dữ liệu doanh thu", response.getMessage());
        assertTrue(response.getData().isEmpty());
    }
}
