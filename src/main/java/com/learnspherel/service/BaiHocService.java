package com.learnspherel.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnspherel.dto.ApiResponse;
import com.learnspherel.dto.BaiHocDto;
import com.learnspherel.entity.BaiHoc;
import com.learnspherel.entity.ChuongTrinh;
import com.learnspherel.entity.KhoaHoc;
import com.learnspherel.entity.NguoiDung;
import com.learnspherel.entity.enums.VaiTro;
import com.learnspherel.exception.BaiHocNotFoundException;
import com.learnspherel.exception.ChuongTrinhNotFoundException;
import com.learnspherel.exception.KhoaHocNotFoundException;
import com.learnspherel.mapper.BaiHocMapper;
import com.learnspherel.repository.BaiHocRepository;
import com.learnspherel.repository.ChuongTrinhRepository;
import com.learnspherel.repository.KhoaHocRepository;
import com.learnspherel.repository.NguoiDungRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
//@AllArgsConstructor
@Transactional
public class BaiHocService {

    private final BaiHocRepository baiHocRepository;
    private final KhoaHocRepository khoaHocRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final BaiHocMapper baiHocMapper;
    private final MessageSource messageSource;
    private final ChuongTrinhRepository chuongTrinhRepository;
    @Value("${learnspherel.upload.dir}")
    private String uploadDirBase;

    public BaiHocService(BaiHocRepository baiHocRepository, KhoaHocRepository khoaHocRepository, NguoiDungRepository nguoiDungRepository, BaiHocMapper baiHocMapper, MessageSource messageSource, ChuongTrinhRepository chuongTrinhRepository) {
        this.baiHocRepository = baiHocRepository;
        this.khoaHocRepository = khoaHocRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.baiHocMapper = baiHocMapper;
        this.messageSource = messageSource;
        this.chuongTrinhRepository = chuongTrinhRepository;
    }

    /**
     * Tạo bài học mới
     *
     * @param baiHocDto Thông tin bài học (maKhoaHoc, tieuDe, thuTuBaiHoc, noiDung)
     * @return ApiResponse Thông tin phản hồi với dữ liệu BaiHocDto
     * @throws RuntimeException Nếu khóa học không tồn tại hoặc giảng viên không hợp lệ
     */
    public ApiResponse<BaiHocDto> createBaiHoc(BaiHocDto baiHocDto) {
        try {
            Optional<KhoaHoc> khoaHocOptional = khoaHocRepository.findById(baiHocDto.getMaKhoaHoc());
            if (!khoaHocOptional.isPresent()) {
                return buildResponse(false, HttpStatus.NOT_FOUND,
                        messageSource.getMessage("khoahoc.notfound", null, "Course not found", null), null);
            }
            KhoaHoc khoaHoc = khoaHocOptional.get();
            Optional<NguoiDung> giangVien = nguoiDungRepository.findById(khoaHoc.getGiangVien().getMaNguoiDung());
            if (!giangVien.isPresent() || giangVien.get().getVaiTro() != VaiTro.GIANG_VIEN) {
                return buildResponse(false, HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("baihoc.create.giangvien.invalid", null, "Invalid instructor", null), null);
            }
            BaiHoc baiHoc = baiHocMapper.baiHocDtoToBaiHoc(baiHocDto);
            baiHocRepository.save(baiHoc);
            BaiHocDto responseDto = baiHocMapper.baiHocToBaiHocDto(baiHoc);
            return buildResponse(true, HttpStatus.CREATED,
                    messageSource.getMessage("baihoc.create.success", null, "Lesson created successfully", null), responseDto);
        } catch (Exception e) {
            throw new RuntimeException(
                    messageSource.getMessage("baihoc.create.failure", null, "Failed to create lesson", null), e);
        }
    }

    @Transactional
    public ApiResponse<Void> batchLessons(Long maChuongTrinh, String lessonsJson, List<MultipartFile> videos) throws IOException {
        ChuongTrinh chuong = chuongTrinhRepository.findById(maChuongTrinh)
                .orElseThrow(() -> new ChuongTrinhNotFoundException("Không tìm thấy chương"));
        // 1. Parse list BaiHocDto từ JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        List<BaiHocDto> lessonDtos;
        try {
            lessonDtos = objectMapper.readValue(lessonsJson, new TypeReference<List<BaiHocDto>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Lỗi parse bài học");
        }
        KhoaHoc khoaHoc = khoaHocRepository.findById(lessonDtos.get(0).getMaKhoaHoc())
                .orElseThrow(() -> new KhoaHocNotFoundException("Không tìm thấy khoá học"));

        // 2. Xoá toàn bộ bài học cũ (hoặc update nếu bạn muốn)
        baiHocRepository.deleteAllByChuongTrinh(chuong);

        // 3. Lưu lại từng bài học mới, upload video nếu có
        int videoIdx = 0;
        for (BaiHocDto dto : lessonDtos) {
            BaiHoc entity = new BaiHoc();
            entity.setChuongTrinh(chuong);
            entity.setTieuDe(dto.getTieuDe());
            entity.setThuTuBaiHoc(dto.getThuTuBaiHoc());
            entity.setKhoaHoc(khoaHoc);

            // Nếu có video (theo thứ tự, null nếu không upload mới)
            if (videos != null && videoIdx < videos.size() && videos.get(videoIdx) != null && !videos.get(videoIdx).isEmpty()) {
                MultipartFile video = videos.get(videoIdx);
                String fileName = UUID.randomUUID() + "_" + video.getOriginalFilename();
                String uploadDir = uploadDirBase + "uploads/video/lesson/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
                Path filePath = uploadPath.resolve(fileName);
                try {
                    Files.write(filePath, video.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                } catch (IOException ex) {
                    throw new RuntimeException("Lỗi upload video: " + ex.getMessage());
                }
                entity.setVideoUrl("/uploads/video/lesson/" + fileName);
            } else if (dto.getVideoUrl() != null) {
                entity.setVideoUrl(dto.getVideoUrl());
            }
            baiHocRepository.save(entity);
            videoIdx++;
        }

        return buildResponse(true, HttpStatus.OK,
                messageSource.getMessage("baihoc.batch.success", null, "Cập nhật danh sách bài học thành công", null), null);
    }

    public ApiResponse<List<BaiHocDto>> getLessonsByChuong(Long maChuongTrinh) {
        List<BaiHoc> lessons = baiHocRepository.findAllByChuongTrinh_MaChuongTrinhOrderByThuTuBaiHocAsc(maChuongTrinh);
        List<BaiHocDto> dtos = lessons.stream().map(this::mapToDto).collect(Collectors.toList());
        return buildResponse(true, HttpStatus.OK, "OK", dtos);
    }

    private BaiHocDto mapToDto(BaiHoc entity) {
        return baiHocMapper.baiHocToBaiHocDto(entity);
    }

    /**
     * Lấy thông tin bài học theo ID
     *
     * @param id ID của bài học
     * @return ApiResponse Thông tin bài học kèm danh sách tài liệu
     * @throws BaiHocNotFoundException Nếu bài học không tồn tại
     */
    public ApiResponse<BaiHocDto> getBaiHocById(Long id) {
        try {
            Optional<BaiHoc> baiHoc = baiHocRepository.findById(id);
            if (!baiHoc.isPresent()) {
                throw new BaiHocNotFoundException(
                        messageSource.getMessage("baihoc.notfound", null, "Lesson not found", null));
            }
            BaiHocDto baiHocDto = baiHocMapper.baiHocToBaiHocDto(baiHoc.get());
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("baihoc.get.success", null, "Lesson retrieved successfully", null), baiHocDto);
        } catch (BaiHocNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    messageSource.getMessage("baihoc.get.failure", null, "Failed to retrieve lesson", null), e);
        }
    }

    /**
     * Lấy danh sách bài học theo khóa học
     *
     * @param maKhoaHoc ID của khóa học
     * @return ApiResponse Danh sách BaiHocDto kèm danh sách tài liệu
     */
    public ApiResponse<List<BaiHocDto>> getAllBaiHocByKhoaHoc(Long maKhoaHoc) {
        try {
            Optional<KhoaHoc> khoaHoc = khoaHocRepository.findById(maKhoaHoc);
            if (!khoaHoc.isPresent()) {
                return buildResponse(false, HttpStatus.NOT_FOUND,
                        messageSource.getMessage("khoahoc.notfound", null, "Course not found", null), null);
            }
            List<BaiHocDto> baiHocDtos = baiHocRepository.findByKhoaHocMaKhoaHoc(maKhoaHoc)
                    .stream()
                    .map(baiHocMapper::baiHocToBaiHocDto)
                    .collect(Collectors.toList());
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("baihoc.getall.success", null, "Lessons retrieved successfully", null), baiHocDtos);
        } catch (Exception e) {
            throw new RuntimeException(
                    messageSource.getMessage("baihoc.getall.failure", null, "Failed to retrieve lessons", null), e);
        }
    }

    /**
     * Cập nhật thông tin bài học
     *
     * @param id        ID của bài học
     * @param baiHocDto Thông tin cập nhật
     * @return ApiResponse Thông tin bài học sau cập nhật
     * @throws BaiHocNotFoundException Nếu bài học không tồn tại
     */
    public ApiResponse<BaiHocDto> updateBaiHoc(Long id, BaiHocDto baiHocDto) {
        try {
            Optional<BaiHoc> baiHoc = baiHocRepository.findById(id);
            if (!baiHoc.isPresent()) {
                throw new BaiHocNotFoundException(
                        messageSource.getMessage("baihoc.notfound", null, "Lesson not found", null));
            }
            Optional<KhoaHoc> khoaHocOptional = khoaHocRepository.findById(baiHocDto.getMaKhoaHoc());
            if (!khoaHocOptional.isPresent()) {
                return buildResponse(false, HttpStatus.NOT_FOUND,
                        messageSource.getMessage("khoahoc.notfound", null, "Course not found", null), null);
            }
            KhoaHoc khoaHoc = khoaHocOptional.get();
            Optional<NguoiDung> giangVien = nguoiDungRepository.findById(khoaHoc.getGiangVien().getMaNguoiDung());
            if (!giangVien.isPresent() || giangVien.get().getVaiTro() != VaiTro.GIANG_VIEN) {
                return buildResponse(false, HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("baihoc.update.giangvien.invalid", null, "Invalid instructor", null), null);
            }
            BaiHoc updatedBaiHoc = baiHocMapper.baiHocDtoToBaiHoc(baiHocDto);
            updatedBaiHoc.setMaBaiHoc(id);
            baiHocRepository.save(updatedBaiHoc);
            BaiHocDto responseDto = baiHocMapper.baiHocToBaiHocDto(updatedBaiHoc);
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("baihoc.update.success", null, "Lesson updated successfully", null), responseDto);
        } catch (BaiHocNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    messageSource.getMessage("baihoc.update.failure", null, "Failed to update lesson", null), e);
        }
    }

    /**
     * Xóa bài học
     *
     * @param id ID của bài học
     * @return ApiResponse Thông tin phản hồi
     * @throws BaiHocNotFoundException Nếu bài học không tồn tại
     */
    public ApiResponse<Void> deleteBaiHoc(Long id) {
        try {
            Optional<BaiHoc> baiHoc = baiHocRepository.findById(id);
            if (!baiHoc.isPresent()) {
                throw new BaiHocNotFoundException(
                        messageSource.getMessage("baihoc.notfound", null, "Lesson not found", null));
            }
            baiHocRepository.delete(baiHoc.get());
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("baihoc.delete.success", null, "Lesson deleted successfully", null), null);
        } catch (BaiHocNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    messageSource.getMessage("baihoc.delete.failure", null, "Failed to delete lesson", null), e);
        }
    }

    private <T> ApiResponse<T> buildResponse(boolean isSuccess, HttpStatus status, String message, T data) {
        return ApiResponse.<T>builder()
                .success(isSuccess)
                .statusCode(status.value())
                .message(message)
                .timestamp(System.currentTimeMillis())
                .data(data)
                .build();
    }
}