package com.learnspherel.service;

import com.learnspherel.dto.ApiResponse;
import com.learnspherel.dto.KhoaHocRequest;
import com.learnspherel.entity.*;
import com.learnspherel.entity.enums.TrangThaiHocTap;
import com.learnspherel.entity.enums.TrangThaiThanhToan;
import com.learnspherel.entity.enums.VaiTro;
import com.learnspherel.repository.*;
import com.learnspherel.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class KhoaHocServiceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private KhoaHocRepository khoaHocRepository;

    @Autowired
    private KyNangRepository kyNangRepository;

    @Autowired
    private GioHangRepository gioHangRepository;

    @Autowired
    private DangKyKhoaHocRepository dangKyKhoaHocRepository;

    @Autowired
    private TienDoHocTapRepository tienDoHocTapRepository;

    @Autowired
    private BaiHocRepository baiHocRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private String instructorToken;
    private String studentToken;
    private NguoiDung instructor;
    private NguoiDung student;
    private KhoaHoc khoaHoc;
    private KyNang kyNang;
    private BaiHoc baiHoc;
    private DangKyKhoaHoc dangKyKhoaHoc;
    private TienDoHocTap tienDoHocTap;

    @BeforeEach
    void setUp() {
        // Xóa dữ liệu cũ
        dangKyKhoaHocRepository.deleteAll();
        tienDoHocTapRepository.deleteAll();
        baiHocRepository.deleteAll();
        gioHangRepository.deleteAll();
        khoaHocRepository.deleteAll();
        kyNangRepository.deleteAll();
        nguoiDungRepository.deleteAll();

        // Tạo dữ liệu mẫu
        instructor = new NguoiDung();
        instructor.setTenDangNhap("instructor");
        instructor.setEmail("instructor@example.com");
        instructor.setMatKhau(passwordEncoder.encode("Password123!"));
        instructor.setVaiTro(VaiTro.GIANG_VIEN);
        instructor.setTrangThai(NguoiDung.TrangThai.HOAT_DONG);
        nguoiDungRepository.save(instructor);

        student = new NguoiDung();
        student.setTenDangNhap("student");
        student.setEmail("student@example.com");
        student.setMatKhau(passwordEncoder.encode("Password123!"));
        student.setVaiTro(VaiTro.HOC_VIEN);
        student.setTrangThai(NguoiDung.TrangThai.HOAT_DONG);
        nguoiDungRepository.save(student);

        khoaHoc = new KhoaHoc();
        khoaHoc.setTieuDe("Test Course");
        khoaHoc.setGiangVien(instructor);
        khoaHoc.setGia(100.0);
        khoaHoc.setCoPhi(true);
        khoaHoc.setVideoDemoUrl("/uploads/video/demo/test.mp4");
        khoaHocRepository.save(khoaHoc);

        kyNang = new KyNang();
        kyNang.setTenKyNang("Java");
        kyNangRepository.save(kyNang);

        baiHoc = new BaiHoc();
        baiHoc.setKhoaHoc(khoaHoc);
        baiHoc.setTieuDe("Lesson 1");
        baiHoc.setThoiLuong(1.5);
        baiHocRepository.save(baiHoc);

        dangKyKhoaHoc = new DangKyKhoaHoc();
        dangKyKhoaHoc.setNguoiDung(student);
        dangKyKhoaHoc.setKhoaHoc(khoaHoc);
        dangKyKhoaHoc.setNgayDangKy(LocalDateTime.now());
        dangKyKhoaHoc.setTrangThaiThanhToan(TrangThaiThanhToan.HOAN_THANH);
        dangKyKhoaHocRepository.save(dangKyKhoaHoc);

        tienDoHocTap = new TienDoHocTap();
        tienDoHocTap.setNguoiDung(student);
        tienDoHocTap.setKhoaHoc(khoaHoc);
        tienDoHocTap.setBaiHoc(baiHoc);
        tienDoHocTap.setTrangThai(TrangThaiHocTap.HOAN_THANH);
        tienDoHocTapRepository.save(tienDoHocTap);

        // Tạo token
        instructorToken = jwtUtil.generateToken(new org.springframework.security.core.userdetails.User(
                "instructor", "Password123!", Arrays.asList()));
        studentToken = jwtUtil.generateToken(new org.springframework.security.core.userdetails.User(
                "student", "Password123!", Arrays.asList()));
    }

    private HttpEntity<?> createEntityWithToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(headers);
    }

    private HttpEntity<?> createEntityWithToken(Object body, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    private HttpEntity<MultiValueMap<String, Object>> createMultipartEntity(MultipartFile file, String token) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });
        return new HttpEntity<>(body, headers);
    }

    @Test
    void testGetAllKhoaHoc() {
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/khoa-hoc",
                HttpMethod.GET,
                createEntityWithToken(studentToken),
                ApiResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Lấy danh sách khóa học thành công", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
    }

    @Test
    void testGetKhoaHocById() {
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/khoa-hoc/" + khoaHoc.getMaKhoaHoc(),
                HttpMethod.GET,
                createEntityWithToken(studentToken),
                ApiResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Lấy khóa học thành công", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
    }

    @Test
    void testGetKhoaHocById_NotFound() {
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/khoa-hoc/999",
                HttpMethod.GET,
                createEntityWithToken(studentToken),
                ApiResponse.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Course not found", response.getBody().getMessage());
    }

    @Test
    void testCreateKhoaHoc() {
        KhoaHocRequest request = new KhoaHocRequest();
        request.setTieuDe("New Course");
        request.setMaGiangVien(instructor.getMaNguoiDung());
        request.setGia(200.0);
        request.setCoPhi(true);

        ResponseEntity<ApiResponse> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/khoa-hoc",
                HttpMethod.POST,
                createEntityWithToken(request, instructorToken),
                ApiResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Tạo khóa học thành công", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
    }

    @Test
    void testUpdateKhoaHoc() {
        KhoaHocRequest request = new KhoaHocRequest();
        request.setTieuDe("Updated Course");
        request.setMaGiangVien(instructor.getMaNguoiDung());
        request.setGia(150.0);
        request.setCoPhi(true);

        ResponseEntity<ApiResponse> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/khoa-hoc/" + khoaHoc.getMaKhoaHoc(),
                HttpMethod.PUT,
                createEntityWithToken(request, instructorToken),
                ApiResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Cập nhật khóa học thành công", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
    }

    @Test
    void testDeleteKhoaHoc() {
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/khoa-hoc/" + khoaHoc.getMaKhoaHoc(),
                HttpMethod.DELETE,
                createEntityWithToken(instructorToken),
                ApiResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Xóa khóa học thành công", response.getBody().getMessage());
    }

    @Test
    void testAddKyNangToKhoaHoc() {
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/khoa-hoc/" + khoaHoc.getMaKhoaHoc() + "/ky-nang/" + kyNang.getMaKyNang(),
                HttpMethod.POST,
                createEntityWithToken(instructorToken),
                ApiResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Thêm kỹ năng thành công", response.getBody().getMessage());
    }

    @Test
    void testThemVaoGioHang() {
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/khoa-hoc/" + khoaHoc.getMaKhoaHoc() + "/gio-hang",
                HttpMethod.POST,
                createEntityWithToken(studentToken),
                ApiResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Thêm vào giỏ hàng thành công", response.getBody().getMessage());
    }

    @Test
    void testUploadAnhDaiDien() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image".getBytes());
        HttpEntity<MultiValueMap<String, Object>> entity = createMultipartEntity(file, instructorToken);

        ResponseEntity<ApiResponse> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/khoa-hoc/" + khoaHoc.getMaKhoaHoc() + "/upload-anh",
                HttpMethod.POST,
                entity,
                ApiResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Upload ảnh đại diện thành công", response.getBody().getMessage());
    }

    @Test
    void testUploadVideoDemo() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", "test video".getBytes());
        HttpEntity<MultiValueMap<String, Object>> entity = createMultipartEntity(file, instructorToken);

        ResponseEntity<ApiResponse> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/khoa-hoc/" + khoaHoc.getMaKhoaHoc() + "/upload-video",
                HttpMethod.POST,
                entity,
                ApiResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Upload video demo thành công", response.getBody().getMessage());
    }

    @Test
    void testGetVideoDemoUrl() {
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/khoa-hoc/" + khoaHoc.getMaKhoaHoc() + "/video-demo",
                HttpMethod.GET,
                createEntityWithToken(studentToken),
                ApiResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Video demo retrieved successfully", response.getBody().getMessage());
        assertEquals("/uploads/video/demo/test.mp4", response.getBody().getData());
    }

    @Test
    void testGetMyCourses() {
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/khoa-hoc/my-courses",
                HttpMethod.GET,
                createEntityWithToken(studentToken),
                ApiResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Lấy danh sách khóa học thành công", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
    }

    @Test
    void testGetRevenueByMonth() {
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/khoa-hoc/revenue?year=2025&month=7",
                HttpMethod.GET,
                createEntityWithToken(instructorToken),
                ApiResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Lấy dữ liệu doanh thu thành công", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
    }
}