package com.learnspherel.service;

import com.learnspherel.config.FileStorageProperties;
import com.learnspherel.dto.*;
import com.learnspherel.entity.*;
import com.learnspherel.entity.enums.LoaiTaiLieu;
import com.learnspherel.entity.enums.TrangThaiHocTap;
import com.learnspherel.exception.ChuongTrinhNotFoundException;
import com.learnspherel.exception.KhoaHocNotFoundException;
import com.learnspherel.mapper.BaiHocMapper;
import com.learnspherel.mapper.ChuongTrinhMapper;
import com.learnspherel.repository.*;
import com.learnspherel.utils.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChuongTrinhService {

    private final ChuongTrinhRepository chuongTrinhRepository;
    private final KhoaHocRepository khoaHocRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final BaiHocRepository baiHocRepository;
    private final TaiLieuRepository taiLieuRepository;
    private final TienDoHocTapRepository tienDoHocTapRepository;
    private final ChuongTrinhMapper chuongTrinhMapper;
    private final BaiHocMapper baiHocMapper;
    private final JwtUtil jwtUtil;
    private final MessageSource messageSource;
    private final FileStorageProperties fileStorageProperties;
    @Value("${learnspherel.upload.dir}")
    private String uploadDirBase;

    public ChuongTrinhService(ChuongTrinhRepository chuongTrinhRepository,
                              KhoaHocRepository khoaHocRepository,
                              NguoiDungRepository nguoiDungRepository,
                              BaiHocRepository baiHocRepository,
                              TaiLieuRepository taiLieuRepository,
                              TienDoHocTapRepository tienDoHocTapRepository,
                              ChuongTrinhMapper chuongTrinhMapper,
                              BaiHocMapper baiHocMapper,
                              JwtUtil jwtUtil,
                              MessageSource messageSource,
                              FileStorageProperties fileStorageProperties) {
        this.chuongTrinhRepository = chuongTrinhRepository;
        this.khoaHocRepository = khoaHocRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.baiHocRepository = baiHocRepository;
        this.taiLieuRepository = taiLieuRepository;
        this.tienDoHocTapRepository = tienDoHocTapRepository;
        this.chuongTrinhMapper = chuongTrinhMapper;
        this.baiHocMapper = baiHocMapper;
        this.jwtUtil = jwtUtil;
        this.messageSource = messageSource;
        this.fileStorageProperties = fileStorageProperties;
    }

    @Transactional
    public ApiResponse<ChuongTrinhDto> createChuong(ChuongTrinhDto dto) {
        // Kiểm tra khoá học hợp lệ
        KhoaHoc kh = khoaHocRepository.findById(dto.getMaKhoaHoc())
                .orElseThrow(() -> new KhoaHocNotFoundException(messageSource.getMessage("khoahoc.notfound", null, "Không tìm thấy khoá học", null)));

        ChuongTrinh entity = new ChuongTrinh();
        entity.setKhoaHoc(kh);
        entity.setTieuDe(dto.getTieuDe());
        entity.setMoTa(dto.getMoTa());
        entity.setThuTuChuong(dto.getThuTuChuong());
        entity.setNgayTao(LocalDateTime.now());
        entity = chuongTrinhRepository.save(entity);

        ChuongTrinhDto response = chuongTrinhMapper.toDto(entity);
        return buildResponse(true, HttpStatus.CREATED,
                messageSource.getMessage("chuong.create.success", null, "Tạo chương thành công!", null), response);
    }

    @Transactional
    public ApiResponse<ChuongTrinhDto> updateChuong(Long maChuongTrinh, ChuongTrinhDto dto) {
        ChuongTrinh entity = chuongTrinhRepository.findById(maChuongTrinh)
                .orElseThrow(() -> new ChuongTrinhNotFoundException(messageSource.getMessage("chuong.notfound", null, "Không tìm thấy chương", null)));

        entity.setTieuDe(dto.getTieuDe());
        entity.setMoTa(dto.getMoTa());
        entity.setThuTuChuong(dto.getThuTuChuong());
        entity.setNgayCapNhat(LocalDateTime.now());
        entity = chuongTrinhRepository.save(entity);

        ChuongTrinhDto response = chuongTrinhMapper.toDto(entity);
        return buildResponse(true, HttpStatus.OK,
                messageSource.getMessage("chuong.update.success", null, "Cập nhật chương thành công!", null), response);
    }

    @Transactional
    public ApiResponse<Void> deleteChuong(Long maChuongTrinh) {
        ChuongTrinh entity = chuongTrinhRepository.findById(maChuongTrinh)
                .orElseThrow(() -> new ChuongTrinhNotFoundException(messageSource.getMessage("chuong.notfound", null, "Không tìm thấy chương", null)));
        chuongTrinhRepository.delete(entity);
        return buildResponse(true, HttpStatus.OK,
                messageSource.getMessage("chuong.delete.success", null, "Đã xoá chương!", null), null);
    }
//    public ApiResponse<ChuongTrinhDto> createChuongTrinh(ChuongTrinhDto request, String token) {
//        try {
//            String username = jwtUtil.extractUsername(token);
//            NguoiDung user = nguoiDungRepository.findByUsernameOrEmail(username)
//                    .orElseThrow(() -> new UsernameNotFoundException(messageSource.getMessage("auth.login.username.notfound", null, "Người dùng không tồn tại", null)));
//
//            KhoaHoc khoaHoc = khoaHocRepository.findById(request.getMaKhoaHoc())
//                    .orElseThrow(() -> new KhoaHocNotFoundException(messageSource.getMessage("khoahoc.notfound", null, "Khóa học không tồn tại", null)));
//
//            if (user.getVaiTro().name().equals("GIANG_VIEN") && !khoaHoc.getGiangVien().getMaNguoiDung().equals(user.getMaNguoiDung())) {
//                return buildResponse(false, HttpStatus.FORBIDDEN,
//                        messageSource.getMessage("chuongtrinh.create.giangvien.invalid", null, "Giảng viên không hợp lệ", null), null);
//            }
//
//            ChuongTrinh chuongTrinh = chuongTrinhMapper.toEntity(request);
//            chuongTrinh.setKhoaHoc(khoaHoc);
//            chuongTrinhRepository.save(chuongTrinh);
//            ChuongTrinhDto responseDto = chuongTrinhMapper.toDto(chuongTrinh);
//            return buildResponse(true, HttpStatus.CREATED,
//                    messageSource.getMessage("chuongtrinh.create.success", null, "Tạo chương trình thành công", null), responseDto);
//        } catch (Exception e) {
//            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
//                    messageSource.getMessage("chuongtrinh.create.failure", null, "Tạo chương trình thất bại", null), null);
//        }
//    }

    public ApiResponse<ChuongTrinhDto> getChuongTrinh(Long id) {
        try {
            ChuongTrinh chuongTrinh = chuongTrinhRepository.findById(id)
                    .orElseThrow(() -> new ChuongTrinhNotFoundException(messageSource.getMessage("chuongtrinh.notfound", null, "Chương trình không tồn tại", null)));
            ChuongTrinhDto responseDto = chuongTrinhMapper.toDto(chuongTrinh);
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("chuongtrinh.get.success", null, "Lấy chương trình thành công", null), responseDto);
        } catch (ChuongTrinhNotFoundException e) {
            return buildResponse(false, HttpStatus.NOT_FOUND,
                    messageSource.getMessage("chuongtrinh.notfound", null, "Không tìm thấy chương trình", null), null);
        } catch (Exception e) {
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("chuongtrinh.get.failure", null, "Lấy chương trình thất bại", null), null);
        }
    }

    public ApiResponse<List<ChuongTrinhDto>> getChuongTrinhByKhoaHoc(Long maKhoaHoc) {
        try {
            khoaHocRepository.findById(maKhoaHoc)
                    .orElseThrow(() -> new KhoaHocNotFoundException(messageSource.getMessage("khoahoc.notfound", null, "Khóa học không tồn tại", null)));
            List<ChuongTrinh> chuongTrinhs = chuongTrinhRepository.findByKhoaHocMaKhoaHoc(maKhoaHoc);
            if (chuongTrinhs.isEmpty()) {
                return buildResponse(false, HttpStatus.NOT_FOUND,
                        messageSource.getMessage("chuongtrinh.notfound", null, "Không có chương trình trong khoá học này", null), null);
            }
            List<ChuongTrinhDto> responseDtos = chuongTrinhs.stream()
                    .map(chuongTrinhMapper::toDto)
                    .collect(Collectors.toList());
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("chuongtrinh.getall.success", null, "Lấy danh sách chương trình thành công", null), responseDtos);
        } catch (KhoaHocNotFoundException e) {
            return buildResponse(false, HttpStatus.NOT_FOUND,
                    messageSource.getMessage("khoahoc.notfound", null, "Không tìm thấy khóa học", null), null);
        } catch (Exception e) {
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("chuongtrinh.getall.failure", null, "Lấy danh sách chương trình thất bại", null), null);
        }
    }

    public ApiResponse<ChuongTrinhDto> updateChuongTrinh(Long id, ChuongTrinhDto request, String token, List<MultipartFile> lessonFiles) {
        try {
            String username = jwtUtil.extractUsername(token);
            NguoiDung user = nguoiDungRepository.findByUsernameOrEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException(messageSource.getMessage("auth.login.username.notfound", null, "Người dùng không tồn tại", null)));

            ChuongTrinh chuongTrinh = chuongTrinhRepository.findById(id)
                    .orElseThrow(() -> new ChuongTrinhNotFoundException(messageSource.getMessage("chuongtrinh.notfound", null, "Chương trình không tồn tại", null)));
            KhoaHoc khoaHoc = khoaHocRepository.findById(request.getMaKhoaHoc())
                    .orElseThrow(() -> new KhoaHocNotFoundException(messageSource.getMessage("khoahoc.notfound", null, "Khóa học không tồn tại", null)));

            if (user.getVaiTro().name().equals("GIANG_VIEN") && !khoaHoc.getGiangVien().getMaNguoiDung().equals(user.getMaNguoiDung())) {
                return buildResponse(false, HttpStatus.FORBIDDEN,
                        messageSource.getMessage("chuongtrinh.update.giangvien.invalid", null, "Giảng viên không hợp lệ", null), null);
            }

            // Update chapter details
            chuongTrinh.setTieuDe(request.getTieuDe());
            chuongTrinh.setThuTuChuong(request.getThuTuChuong());
            chuongTrinh.setMoTa(request.getMoTa());
            chuongTrinh.setKhoaHoc(khoaHoc);

            // Handle lessons
            List<BaiHoc> existingLessons = baiHocRepository.findByChuongTrinhMaChuongTrinh(id);
            // Remove lessons not in the request
            existingLessons.forEach(existingLesson -> {
                if (request.getBaiHocs() == null || request.getBaiHocs().stream()
                        .noneMatch(dto -> dto.getMaBaiHoc() != null && dto.getMaBaiHoc().equals(existingLesson.getMaBaiHoc()))) {
                    // Delete associated resources
                    List<TaiLieu> taiLieus = taiLieuRepository.findByBaiHocMaBaiHoc(existingLesson.getMaBaiHoc());
                    taiLieus.forEach(taiLieu -> {
                        try {
                            Files.deleteIfExists(Paths.get(uploadDirBase + taiLieu.getDuongDan().substring(1)));
                        } catch (IOException e) {
                            // Log error but continue
                        }
                        taiLieuRepository.delete(taiLieu);
                    });
                    baiHocRepository.delete(existingLesson);
                }
            });

            // Update or create lessons
            if (request.getBaiHocs() != null) {
                for (int i = 0; i < request.getBaiHocs().size(); i++) {
                    BaiHocDto baiHocDto = request.getBaiHocs().get(i);
                    BaiHoc baiHoc;
                    if (baiHocDto.getMaBaiHoc() != null) {
                        // Update existing lesson
                        baiHoc = baiHocRepository.findById(baiHocDto.getMaBaiHoc())
                                .orElseThrow(() -> new RuntimeException("Bài học không tồn tại"));
                        baiHoc.setTieuDe(baiHocDto.getTieuDe());
                        baiHoc.setThuTuBaiHoc(baiHocDto.getThuTuBaiHoc());
                    } else {
                        // Create new lesson
                        baiHoc = baiHocMapper.baiHocDtoToBaiHoc(baiHocDto);
                        baiHoc.setChuongTrinh(chuongTrinh);
                        baiHoc.setKhoaHoc(khoaHoc);
                    }
                    System.out.println("lesson" + lessonFiles.size());
                    // Handle video file
                    if (lessonFiles != null && i < lessonFiles.size() && lessonFiles.get(i) != null && !lessonFiles.get(i).isEmpty()) {
                        MultipartFile file = lessonFiles.get(i);
                        long maxSizeInBytes = convertToBytes(fileStorageProperties.getMaxFileSize());
                        if (file.getSize() > maxSizeInBytes) {
                            return buildResponse(false, HttpStatus.BAD_REQUEST,
                                    messageSource.getMessage("baihoc.upload.file.too.large", new Object[]{fileStorageProperties.getMaxFileSize()}, "File video vượt quá kích thước tối đa", null), null);
                        }

                        String contentType = file.getContentType();
                        if (!isValidVideoType(contentType)) {
                            return buildResponse(false, HttpStatus.BAD_REQUEST,
                                    messageSource.getMessage("baihoc.upload.file.invalid", null, "Định dạng file video không hợp lệ", null), null);
                        }

                        String uploadDir = uploadDirBase + "uploads/video/lessons/";
                        Path uploadPath = Paths.get(uploadDir);
                        if (!Files.exists(uploadPath)) {
                            Files.createDirectories(uploadPath);
                        }

                        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                        Path filePath = uploadPath.resolve(fileName);
                        Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                        // Delete old video if exists
                        List<TaiLieu> existingTaiLieus = taiLieuRepository.findByBaiHocMaBaiHocAndLoaiFile(baiHoc.getMaBaiHoc(), LoaiTaiLieu.VIDEO);
                        existingTaiLieus.forEach(taiLieu -> {
                            try {
                                Files.deleteIfExists(Paths.get(uploadDirBase + taiLieu.getDuongDan().substring(1)));
                            } catch (IOException e) {
                                // Log error but continue
                            }
                            taiLieuRepository.delete(taiLieu);
                        });
                        int size = (int) file.getSize();
                        // Save new video
                        TaiLieu taiLieu = TaiLieu.builder()
                                .baiHoc(baiHoc)
                                .tenFile(fileName)
                                .duongDan("/uploads/video/lessons/" + fileName)
                                .loaiFile(LoaiTaiLieu.VIDEO)
                                .kichThuoc(size)
                                .build();
                        taiLieuRepository.save(taiLieu);
                        baiHocDto.setVideoUrl(taiLieu.getDuongDan());
                    }
                    baiHocRepository.save(baiHoc);
                }
            }

            chuongTrinhRepository.save(chuongTrinh);
            ChuongTrinhDto responseDto = chuongTrinhMapper.toDto(chuongTrinh);
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("chuongtrinh.update.success", null, "Cập nhật chương trình thành công", null), responseDto);
        } catch (ChuongTrinhNotFoundException | KhoaHocNotFoundException e) {
            return buildResponse(false, HttpStatus.NOT_FOUND,
                    messageSource.getMessage("chuongtrinh.notfound", null, "Không tìm thấy chương trình", null), null);
        } catch (IOException e) {
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("baihoc.upload.file.failure", null, "Upload file video thất bại", null), null);
        }
//        catch (Exception e) {
//            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
//                    messageSource.getMessage("chuongtrinh.update.failure", null, "Cập nhật chương trình thất bại", null), null);
//        }

    }

    public ApiResponse<List<ChuongTrinhDto>> createOrUpdateChuongTrinhs(List<ChuongTrinhDto> requests, String token, List<MultipartFile> lessonFiles) {
        try {
            System.out.println(requests.toString());
            String username = jwtUtil.extractUsername(token);
            NguoiDung user = nguoiDungRepository.findByUsernameOrEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException(messageSource.getMessage("auth.login.username.notfound", null, "Người dùng không tồn tại", null)));

            List<ChuongTrinhDto> responseDtos = new ArrayList<>();
            int fileIndex = 0; // Track file index across all chapters

            for (ChuongTrinhDto request : requests) {
                KhoaHoc khoaHoc = khoaHocRepository.findById(request.getMaKhoaHoc())
                        .orElseThrow(() -> new KhoaHocNotFoundException(messageSource.getMessage("khoahoc.notfound", null, "Khóa học không tồn tại", null)));

                if (user.getVaiTro().name().equals("GIANG_VIEN") && !khoaHoc.getGiangVien().getMaNguoiDung().equals(user.getMaNguoiDung())) {
                    return buildResponse(false, HttpStatus.FORBIDDEN,
                            messageSource.getMessage("chuongtrinh.update.giangvien.invalid", null, "Giảng viên không hợp lệ", null), null);
                }

                ChuongTrinh chuongTrinh;
                if (request.getMaChuongTrinh() != null) {
                    // Update existing chapter
                    chuongTrinh = chuongTrinhRepository.findById(request.getMaChuongTrinh())
                            .orElseThrow(() -> new ChuongTrinhNotFoundException(messageSource.getMessage("chuongtrinh.notfound", null, "Chương trình không tồn tại", null)));
                    chuongTrinh.setTieuDe(request.getTieuDe());
                    chuongTrinh.setThuTuChuong(request.getThuTuChuong());
                    chuongTrinh.setMoTa(request.getMoTa());
                    chuongTrinh.setKhoaHoc(khoaHoc);
                } else {
                    // Create new chapter
                    chuongTrinh = chuongTrinhMapper.toEntity(request);
                    chuongTrinh.setKhoaHoc(khoaHoc);
                }

                // Handle lessons
                List<BaiHoc> existingLessons = baiHocRepository.findByChuongTrinhMaChuongTrinh(chuongTrinh.getMaChuongTrinh() != null ? chuongTrinh.getMaChuongTrinh() : 0L);
                existingLessons.forEach(existingLesson -> {
                    if (request.getBaiHocs() == null || request.getBaiHocs().stream()
                            .noneMatch(dto -> dto.getMaBaiHoc() != null && dto.getMaBaiHoc().equals(existingLesson.getMaBaiHoc()))) {
                        // Delete associated resources
                        List<TaiLieu> taiLieus = taiLieuRepository.findByBaiHocMaBaiHoc(existingLesson.getMaBaiHoc());
                        taiLieus.forEach(taiLieu -> {
                            try {
                                Files.deleteIfExists(Paths.get(uploadDirBase + taiLieu.getDuongDan().substring(1)));
                            } catch (IOException e) {
                                // Log error but continue
                            }
                            taiLieuRepository.delete(taiLieu);
                        });
                        baiHocRepository.delete(existingLesson);
                    }
                });

                if (request.getBaiHocs() != null) {
                    for (int i = 0; i < request.getBaiHocs().size(); i++) {
                        BaiHocDto baiHocDto = request.getBaiHocs().get(i);
                        BaiHoc baiHoc;
                        if (baiHocDto.getMaBaiHoc() != null) {
                            baiHoc = baiHocRepository.findById(baiHocDto.getMaBaiHoc())
                                    .orElseThrow(() -> new RuntimeException("Bài học không tồn tại"));
                            baiHoc.setTieuDe(baiHocDto.getTieuDe());
                            baiHoc.setThuTuBaiHoc(baiHocDto.getThuTuBaiHoc());
                        } else {
                            baiHoc = baiHocMapper.baiHocDtoToBaiHoc(baiHocDto);
                            baiHoc.setChuongTrinh(chuongTrinh);
                            baiHoc.setKhoaHoc(khoaHoc);
                        }

                        if (fileIndex < lessonFiles.size() && lessonFiles.get(fileIndex) != null && !lessonFiles.get(fileIndex).isEmpty()) {
                            MultipartFile file = lessonFiles.get(fileIndex);
                            long maxSizeInBytes = convertToBytes(fileStorageProperties.getMaxFileSize());
                            if (file.getSize() > maxSizeInBytes) {
                                return buildResponse(false, HttpStatus.BAD_REQUEST,
                                        messageSource.getMessage("baihoc.upload.file.too.large", new Object[]{fileStorageProperties.getMaxFileSize()}, "File video vượt quá kích thước tối đa", null), null);
                            }

                            String contentType = file.getContentType();
                            if (!isValidVideoType(contentType)) {
                                return buildResponse(false, HttpStatus.BAD_REQUEST,
                                        messageSource.getMessage("baihoc.upload.file.invalid", null, "Định dạng file video không hợp lệ", null), null);
                            }

                            String uploadDir = uploadDirBase + "uploads/video/lessons/";
                            Path uploadPath = Paths.get(uploadDir);
                            if (!Files.exists(uploadPath)) {
                                Files.createDirectories(uploadPath);
                            }

                            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                            Path filePath = uploadPath.resolve(fileName);
                            Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                            List<TaiLieu> existingTaiLieus = taiLieuRepository.findByBaiHocMaBaiHocAndLoaiFile(baiHoc.getMaBaiHoc(), LoaiTaiLieu.VIDEO);
                            existingTaiLieus.forEach(taiLieu -> {
                                try {
                                    Files.deleteIfExists(Paths.get(uploadDirBase + taiLieu.getDuongDan().substring(1)));
                                } catch (IOException e) {
                                    // Log error but continue
                                }
                                taiLieuRepository.delete(taiLieu);
                            });
                            int kichThuocFile = (int) file.getSize();
                            TaiLieu taiLieu = TaiLieu.builder()
                                    .baiHoc(baiHoc)
                                    .tenFile(fileName)
                                    .duongDan("/uploads/video/lessons/" + fileName)
                                    .loaiFile(LoaiTaiLieu.VIDEO)
                                    .kichThuoc(kichThuocFile)
                                    .build();
                            taiLieuRepository.save(taiLieu);
                            baiHocDto.setVideoUrl(taiLieu.getDuongDan());
                            // Tính thời lượng video từ file
                            try {
                                File physicalFile = filePath.toFile(); // Đã có từ Files.write(...)
                                MultimediaObject multimediaObject = new MultimediaObject(physicalFile);
                                MultimediaInfo info = multimediaObject.getInfo();

                                long durationMillis = info.getDuration(); // Thời lượng tính bằng milliseconds
                                double durationInMinutes = durationMillis / 60000.0; // Convert sang phút
                                baiHoc.setThoiLuong(durationInMinutes);
                            } catch (Exception e) {
                                // Nếu không đọc được, có thể ghi log hoặc gán mặc định 0.0
                                baiHoc.setThoiLuong(0.0);
                            }
                        }
                        baiHocRepository.save(baiHoc);
                        fileIndex++;
                    }
                }

                chuongTrinhRepository.save(chuongTrinh);
                responseDtos.add(chuongTrinhMapper.toDto(chuongTrinh));
            }

            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("chuongtrinh.batch.success", null, "Cập nhật chương thành công", null), responseDtos);
        } catch (ChuongTrinhNotFoundException | KhoaHocNotFoundException e) {
            return buildResponse(false, HttpStatus.NOT_FOUND,
                    messageSource.getMessage("chuongtrinh.notfound", null, "Không tìm thấy chương trình", null), null);
        } catch (IOException e) {
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("baihoc.upload.file.failure", null, "Upload file video thất bại", null), null);
        } catch (Exception e) {
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("chuongtrinh.batch.failure", null, "Tạo và cập nhật chương thất bại", null), null);
        }
    }

    public ApiResponse<LearningResponse> getChuongHocWithBaiHoc(Long maKhoaHoc, String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            NguoiDung user = nguoiDungRepository.findByUsernameOrEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException(messageSource.getMessage("auth.login.username.notfound", null, "Người dùng không tồn tại", null)));

            KhoaHoc khoaHoc = khoaHocRepository.findById(maKhoaHoc)
                    .orElseThrow(() -> new KhoaHocNotFoundException(messageSource.getMessage("khoahoc.notfound", null, "Khóa học không tồn tại", null)));
            LearningResponse learningResponse = new LearningResponse();
            learningResponse.setMaKhoaHoc(maKhoaHoc);
            learningResponse.setTenKhoaHoc(khoaHoc.getTieuDe());

            List<ChuongTrinh> chuongTrinhs = chuongTrinhRepository.findByKhoaHocMaKhoaHoc(maKhoaHoc);

            if (chuongTrinhs.isEmpty()) {
                return buildResponse(true, HttpStatus.OK,
                        messageSource.getMessage("chuongtrinh.notfound", null, "Không có chương trình trong khoá học này", null), learningResponse);
            }

            List<ChuongHocResponse> responses = chuongTrinhs.stream().map(chuong -> {
                ChuongHocResponse response = chuongTrinhMapper.toResponse(chuong);
                List<BaiHoc> baiHocList = baiHocRepository.findByChuongTrinhMaChuongTrinh(chuong.getMaChuongTrinh());
                List<BaiHocResponse> baiHocResponses = baiHocList.stream().map(baiHoc -> {
                    BaiHocResponse baiHocResponse = baiHocMapper.toResponse(baiHoc);
//                    List<TaiLieu> taiLieuList = taiLieuRepository.findByBaiHocMaBaiHocAndLoaiFile(baiHoc.getMaBaiHoc(), LoaiTaiLieu.VIDEO);
//                    baiHocResponse.setVideoUrl(taiLieuList.isEmpty() ? null : taiLieuList.get(0).getDuongDan());
                    baiHocResponse.setVideoUrl(baiHoc.getVideoUrl());
                    Double thoiLuong = baiHoc.getThoiLuong();
                    if (thoiLuong != null) {
                        int minutes = (int) (thoiLuong * 60);
                        baiHocResponse.setThoiLuong(String.format("%d:%02d", minutes / 60, minutes % 60));
                    } else {
                        baiHocResponse.setThoiLuong("0:00");
                    }

                    List<TienDoHocTap> tienDoList = tienDoHocTapRepository.findByNguoiDungMaNguoiDungAndBaiHocMaBaiHoc(
                            user.getMaNguoiDung(), baiHoc.getMaBaiHoc());
                    String status = "unwatched";
                    if (!tienDoList.isEmpty()) {
                        TienDoHocTap tienDo = tienDoList.get(0);
                        status = tienDo.getTrangThai() == TrangThaiHocTap.HOAN_THANH ? "watched" : "watching";
                    }
                    baiHocResponse.setStatus(status);

                    return baiHocResponse;
                }).collect(Collectors.toList());
                response.setBaiHoc(baiHocResponses);
                return response;
            }).collect(Collectors.toList());
            learningResponse.setChuongHocResponses(responses);
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("chuongtrinh.getall.success", null, "Lấy danh sách chương trình và bài học thành công", null), learningResponse);
        } catch (KhoaHocNotFoundException e) {
            return buildResponse(false, HttpStatus.NOT_FOUND,
                    messageSource.getMessage("khoahoc.notfound", null, "Không tìm thấy khóa học", null), null);
        } catch (UsernameNotFoundException e) {
            return buildResponse(false, HttpStatus.NOT_FOUND,
                    messageSource.getMessage("auth.login.username.notfound", null, "Người dùng không tồn tại", null), null);
        } catch (Exception e) {
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("chuongtrinh.getall.failure", null, "Lấy danh sách chương trình và bài học thất bại", null), null);
        }
    }

    public ApiResponse<Void> deleteChuongTrinh(Long id, String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            NguoiDung user = nguoiDungRepository.findByUsernameOrEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException(messageSource.getMessage("auth.login.username.notfound", null, "Người dùng không tồn tại", null)));

            ChuongTrinh chuongTrinh = chuongTrinhRepository.findById(id)
                    .orElseThrow(() -> new ChuongTrinhNotFoundException(messageSource.getMessage("chuongtrinh.notfound", null, "Chương trình không tồn tại", null)));
            KhoaHoc khoaHoc = chuongTrinh.getKhoaHoc();

            if (!user.getVaiTro().name().equals("GIANG_VIEN") && !user.getVaiTro().name().equals("QUAN_TRI")) {
                return buildResponse(false, HttpStatus.FORBIDDEN,
                        messageSource.getMessage("chuongtrinh.delete.unauthorized", null, "Không có quyền xóa chương trình", null), null);
            }

            if (user.getVaiTro().name().equals("GIANG_VIEN") && !khoaHoc.getGiangVien().getMaNguoiDung().equals(user.getMaNguoiDung())) {
                return buildResponse(false, HttpStatus.FORBIDDEN,
                        messageSource.getMessage("chuongtrinh.delete.giangvien.invalid", null, "Giảng viên không hợp lệ", null), null);
            }

            chuongTrinhRepository.delete(chuongTrinh);
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("chuongtrinh.delete.success", null, "Xóa chương trình thành công", null), null);
        } catch (ChuongTrinhNotFoundException e) {
            return buildResponse(false, HttpStatus.NOT_FOUND,
                    messageSource.getMessage("chuongtrinh.notfound", null, "Không tìm thấy chương trình", null), null);
        } catch (Exception e) {
            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("chuongtrinh.delete.failure", null, "Xóa chương trình thất bại", null), null);
        }
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