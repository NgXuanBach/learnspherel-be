package com.learnspherel.service;

import com.learnspherel.dto.ApiResponse;
import com.learnspherel.dto.BaiKiemTraDto;
import com.learnspherel.entity.*;
import com.learnspherel.entity.enums.LoaiBaiKiemTra;
import com.learnspherel.entity.enums.VaiTro;
import com.learnspherel.exception.BaiKiemTraNotFoundException;
import com.learnspherel.mapper.BaiKiemTraMapper;
import com.learnspherel.mapper.CauHoiMapper;
import com.learnspherel.repository.BaiHocRepository;
import com.learnspherel.repository.BaiKiemTraRepository;
import com.learnspherel.repository.KhoaHocRepository;
import com.learnspherel.repository.NguoiDungRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class BaiKiemTraService {

    private final BaiKiemTraRepository baiKiemTraRepository;
    private final KhoaHocRepository khoaHocRepository;
    private final BaiHocRepository baiHocRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final BaiKiemTraMapper baiKiemTraMapper;
    private final CauHoiMapper cauHoiMapper;
    private final MessageSource messageSource;

    public ApiResponse<BaiKiemTraDto> createBaiKiemTra(BaiKiemTraDto baiKiemTraDto) {
        try {
            Optional<KhoaHoc> khoaHocOptional = khoaHocRepository.findById(baiKiemTraDto.getMaKhoaHoc());
            if (!khoaHocOptional.isPresent()) {
                return buildResponse(false, HttpStatus.NOT_FOUND,
                        messageSource.getMessage("khoahoc.notfound", null, "Course not found", null), null);
            }
            KhoaHoc khoaHoc = khoaHocOptional.get();
            Optional<NguoiDung> giangVien = nguoiDungRepository.findById(khoaHoc.getGiangVien().getMaNguoiDung());
            if (!giangVien.isPresent() || giangVien.get().getVaiTro() != VaiTro.GIANG_VIEN) {
                return buildResponse(false, HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("bai_kiem_tra.create.giangvien.invalid", null, "Invalid instructor", null), null);
            }
            if (baiKiemTraDto.getMaBaiHoc() != null) {
                Optional<BaiHoc> baiHocOptional = baiHocRepository.findById(baiKiemTraDto.getMaBaiHoc());
                if (!baiHocOptional.isPresent()) {
                    return buildResponse(false, HttpStatus.NOT_FOUND,
                            messageSource.getMessage("baihoc.notfound", null, "Lesson not found", null), null);
                }
            }
            BaiKiemTra baiKiemTra = baiKiemTraMapper.toEntity(baiKiemTraDto);
            if (baiKiemTraDto.getCauHois() != null) {
                List<CauHoi> cauHois = baiKiemTraDto.getCauHois().stream()
                        .map(cauHoiMapper::toEntity)
                        .peek(cauHoi -> cauHoi.setBaiKiemTra(baiKiemTra))
                        .collect(Collectors.toList());
                baiKiemTra.setCauHois(cauHois);
            }
            baiKiemTraRepository.save(baiKiemTra);
            BaiKiemTraDto responseDto = baiKiemTraMapper.toDto(baiKiemTra);
            return buildResponse(true, HttpStatus.CREATED,
                    messageSource.getMessage("bai_kiem_tra.create.success", null, "Test created successfully", null), responseDto);
        } catch (Exception e) {
            throw new RuntimeException(
                    messageSource.getMessage("bai_kiem_tra.create.failure", null, "Failed to create test", null), e);
        }
    }

    public ApiResponse<BaiKiemTraDto> getBaiKiemTraById(Long id, Long maKhoaHoc) {
        try {
            Optional<BaiKiemTra> baiKiemTra = baiKiemTraRepository.findByMaBaiKiemTraAndKhoaHocMaKhoaHoc(id, maKhoaHoc);
            if (!baiKiemTra.isPresent()) {
                throw new BaiKiemTraNotFoundException(
                        messageSource.getMessage("bai_kiem_tra.notfound", null, "Test not found", null));
            }
            BaiKiemTraDto baiKiemTraDto = baiKiemTraMapper.toDto(baiKiemTra.get());
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("bai_kiem_tra.get.success", null, "Test retrieved successfully", null), baiKiemTraDto);
        } catch (BaiKiemTraNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    messageSource.getMessage("bai_kiem_tra.get.failure", null, "Failed to retrieve test", null), e);
        }
    }

    public ApiResponse<List<BaiKiemTraDto>> getAllBaiKiemTraByKhoaHocAndLoai(Long maKhoaHoc, LoaiBaiKiemTra loai) {
        try {
            Optional<KhoaHoc> khoaHoc = khoaHocRepository.findById(maKhoaHoc);
            if (!khoaHoc.isPresent()) {
                return buildResponse(false, HttpStatus.NOT_FOUND,
                        messageSource.getMessage("khoahoc.notfound", null, "Course not found", null), null);
            }
            List<BaiKiemTraDto> baiKiemTraDtos = baiKiemTraRepository.findByKhoaHocMaKhoaHocAndLoai(maKhoaHoc, loai)
                    .stream()
                    .map(baiKiemTraMapper::toDto)
                    .collect(Collectors.toList());
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("bai_kiem_tra.getall.success", null, "Tests retrieved successfully", null), baiKiemTraDtos);
        } catch (Exception e) {
            throw new RuntimeException(
                    messageSource.getMessage("bai_kiem_tra.getall.failure", null, "Failed to retrieve tests", null), e);
        }
    }

    public ApiResponse<BaiKiemTraDto> updateBaiKiemTra(Long id, BaiKiemTraDto baiKiemTraDto) {
        try {
            Optional<BaiKiemTra> baiKiemTra = baiKiemTraRepository.findById(id);
            if (!baiKiemTra.isPresent()) {
                throw new BaiKiemTraNotFoundException(
                        messageSource.getMessage("bai_kiem_tra.notfound", null, "Test not found", null));
            }
            Optional<KhoaHoc> khoaHocOptional = khoaHocRepository.findById(baiKiemTraDto.getMaKhoaHoc());
            if (!khoaHocOptional.isPresent()) {
                return buildResponse(false, HttpStatus.NOT_FOUND,
                        messageSource.getMessage("khoahoc.notfound", null, "Course not found", null), null);
            }
            KhoaHoc khoaHoc = khoaHocOptional.get();
            Optional<NguoiDung> giangVien = nguoiDungRepository.findById(khoaHoc.getGiangVien().getMaNguoiDung());
            if (!giangVien.isPresent() || giangVien.get().getVaiTro() != VaiTro.GIANG_VIEN) {
                return buildResponse(false, HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("bai_kiem_tra.update.giangvien.invalid", null, "Invalid instructor", null), null);
            }
            if (baiKiemTraDto.getMaBaiHoc() != null) {
                Optional<BaiHoc> baiHocOptional = baiHocRepository.findById(baiKiemTraDto.getMaBaiHoc());
                if (!baiHocOptional.isPresent()) {
                    return buildResponse(false, HttpStatus.NOT_FOUND,
                            messageSource.getMessage("baihoc.notfound", null, "Lesson not found", null), null);
                }
            }
            BaiKiemTra updatedBaiKiemTra = baiKiemTraMapper.toEntity(baiKiemTraDto);
            updatedBaiKiemTra.setMaBaiKiemTra(id);
            if (baiKiemTraDto.getCauHois() != null) {
                List<CauHoi> cauHois = baiKiemTraDto.getCauHois().stream()
                        .map(cauHoiMapper::toEntity)
                        .peek(cauHoi -> cauHoi.setBaiKiemTra(updatedBaiKiemTra))
                        .collect(Collectors.toList());
                updatedBaiKiemTra.setCauHois(cauHois);
            }
            baiKiemTraRepository.save(updatedBaiKiemTra);
            BaiKiemTraDto responseDto = baiKiemTraMapper.toDto(updatedBaiKiemTra);
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("bai_kiem_tra.update.success", null, "Test updated successfully", null), responseDto);
        } catch (BaiKiemTraNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    messageSource.getMessage("bai_kiem_tra.update.failure", null, "Failed to update test", null), e);
        }
    }

    public ApiResponse<Void> deleteBaiKiemTra(Long id) {
        try {
            Optional<BaiKiemTra> baiKiemTra = baiKiemTraRepository.findById(id);
            if (!baiKiemTra.isPresent()) {
                throw new BaiKiemTraNotFoundException(
                        messageSource.getMessage("bai_kiem_tra.notfound", null, "Test not found", null));
            }
            baiKiemTraRepository.delete(baiKiemTra.get());
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("bai_kiem_tra.delete.success", null, "Test deleted successfully", null), null);
        } catch (BaiKiemTraNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    messageSource.getMessage("bai_kiem_tra.delete.failure", null, "Failed to delete test", null), e);
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