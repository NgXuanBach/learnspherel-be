package com.learnspherel.service;

import com.learnspherel.config.FileStorageProperties;
import com.learnspherel.dto.ApiResponse;
import com.learnspherel.dto.TaiLieuDto;
import com.learnspherel.entity.BaiHoc;
import com.learnspherel.entity.KhoaHoc;
import com.learnspherel.entity.NguoiDung;
import com.learnspherel.entity.TaiLieu;
import com.learnspherel.entity.enums.LoaiTaiLieu;
import com.learnspherel.entity.enums.TrangThaiThanhToan;
import com.learnspherel.entity.enums.VaiTro;
import com.learnspherel.exception.TaiLieuNotFoundException;
import com.learnspherel.mapper.TaiLieuMapper;
import com.learnspherel.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
@Transactional
public class TaiLieuService {

    private final TaiLieuRepository taiLieuRepository;
    private final BaiHocRepository baiHocRepository;
    private final KhoaHocRepository khoaHocRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final DangKyKhoaHocRepository dangKyKhoaHocRepository;
    private final TaiLieuMapper taiLieuMapper;
    private final MessageSource messageSource;
    private final FileStorageProperties fileStorageProperties;
    @Value("${learnspherel.upload.dir}")
    private String uploadDirBase;

    public TaiLieuService(TaiLieuRepository taiLieuRepository, BaiHocRepository baiHocRepository, KhoaHocRepository khoaHocRepository, NguoiDungRepository nguoiDungRepository, DangKyKhoaHocRepository dangKyKhoaHocRepository, TaiLieuMapper taiLieuMapper, MessageSource messageSource, FileStorageProperties fileStorageProperties) {
        this.taiLieuRepository = taiLieuRepository;
        this.baiHocRepository = baiHocRepository;
        this.khoaHocRepository = khoaHocRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.dangKyKhoaHocRepository = dangKyKhoaHocRepository;
        this.taiLieuMapper = taiLieuMapper;
        this.messageSource = messageSource;
        this.fileStorageProperties = fileStorageProperties;
    }

    public ApiResponse<TaiLieuDto> createTaiLieu(TaiLieuDto taiLieuDto) {
        try {
            Optional<BaiHoc> baiHocOptional = baiHocRepository.findById(taiLieuDto.getMaBaiHoc());
            if (!baiHocOptional.isPresent()) {
                return buildResponse(false, HttpStatus.NOT_FOUND,
                        messageSource.getMessage("baihoc.notfound", null, "Lesson not found", null), null);
            }
            BaiHoc baiHoc = baiHocOptional.get();
            Optional<KhoaHoc> khoaHocOptional = khoaHocRepository.findById(baiHoc.getKhoaHoc().getMaKhoaHoc());
            if (!khoaHocOptional.isPresent()) {
                return buildResponse(false, HttpStatus.NOT_FOUND,
                        messageSource.getMessage("khoahoc.notfound", null, "Course not found", null), null);
            }
            KhoaHoc khoaHoc = khoaHocOptional.get();
            Optional<NguoiDung> nguoiDung = nguoiDungRepository.findById(khoaHoc.getGiangVien().getMaNguoiDung());
            if (!nguoiDung.isPresent() || (nguoiDung.get().getVaiTro() != VaiTro.GIANG_VIEN && nguoiDung.get().getVaiTro() != VaiTro.QUAN_TRI)) {
                return buildResponse(false, HttpStatus.FORBIDDEN,
                        messageSource.getMessage("tailieu.create.unauthorized", null, "Unauthorized to create document", null), null);
            }
            TaiLieu taiLieu = taiLieuMapper.taiLieuDtoToTaiLieu(taiLieuDto);
            taiLieu.setBaiHoc(baiHoc);
            taiLieuRepository.save(taiLieu);
            TaiLieuDto responseDto = taiLieuMapper.taiLieuToTaiLieuDto(taiLieu);
            return buildResponse(true, HttpStatus.CREATED,
                    messageSource.getMessage("tailieu.create.success", null, "Document created successfully", null), responseDto);
        } catch (Exception e) {
            throw new RuntimeException(
                    messageSource.getMessage("tailieu.create.failure", null, "Failed to create document", null), e);
        }
    }

    public ApiResponse<TaiLieuDto> getTaiLieuById(Long id) {
        try {
            Optional<TaiLieu> taiLieu = taiLieuRepository.findById(id);
            if (!taiLieu.isPresent()) {
                throw new TaiLieuNotFoundException(
                        messageSource.getMessage("tailieu.notfound", null, "Document not found", null));
            }
            TaiLieuDto taiLieuDto = taiLieuMapper.taiLieuToTaiLieuDto(taiLieu.get());
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("tailieu.get.success", null, "Document retrieved successfully", null), taiLieuDto);
        } catch (TaiLieuNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    messageSource.getMessage("tailieu.get.failure", null, "Failed to retrieve document", null), e);
        }
    }

    public ApiResponse<List<TaiLieuDto>> getTaiLieuByBaiHoc(Long maBaiHoc, String loaiFile) {
        try {
            Optional<BaiHoc> baiHoc = baiHocRepository.findById(maBaiHoc);
            if (!baiHoc.isPresent()) {
                return buildResponse(false, HttpStatus.NOT_FOUND,
                        messageSource.getMessage("baihoc.notfound", null, "Lesson not found", null), null);
            }
            List<TaiLieu> taiLieus;
            if (loaiFile != null && !loaiFile.isEmpty()) {
                try {
                    LoaiTaiLieu loaiTaiLieu = LoaiTaiLieu.valueOf(loaiFile.toUpperCase());
                    taiLieus = taiLieuRepository.findByBaiHocMaBaiHocAndBaiNopIsNullAndLoaiFile(maBaiHoc, loaiTaiLieu);
                } catch (IllegalArgumentException e) {
                    return buildResponse(false, HttpStatus.BAD_REQUEST,
                            messageSource.getMessage("tailieu.loaiFile.invalid", null, "Invalid file type", null), null);
                }
            } else {
                taiLieus = taiLieuRepository.findByBaiHocMaBaiHocAndBaiNopIsNull(maBaiHoc);
            }
            List<TaiLieuDto> taiLieuDtos = taiLieus.stream()
                    .map(taiLieuMapper::taiLieuToTaiLieuDto)
                    .collect(Collectors.toList());
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("tailieu.getall.success", null, "Documents retrieved successfully", null), taiLieuDtos);
        } catch (Exception e) {
            throw new RuntimeException(
                    messageSource.getMessage("tailieu.getall.failure", null, "Failed to retrieve documents", null), e);
        }
    }

    public ApiResponse<TaiLieuDto> updateTaiLieu(Long id, TaiLieuDto taiLieuDto) {
        try {
            Optional<TaiLieu> taiLieu = taiLieuRepository.findById(id);
            if (!taiLieu.isPresent()) {
                throw new TaiLieuNotFoundException(
                        messageSource.getMessage("tailieu.notfound", null, "Document not found", null));
            }
            Optional<BaiHoc> baiHocOptional = baiHocRepository.findById(taiLieuDto.getMaBaiHoc());
            if (!baiHocOptional.isPresent()) {
                return buildResponse(false, HttpStatus.NOT_FOUND,
                        messageSource.getMessage("baihoc.notfound", null, "Lesson not found", null), null);
            }
            BaiHoc baiHoc = baiHocOptional.get();
            Optional<KhoaHoc> khoaHocOptional = khoaHocRepository.findById(baiHoc.getKhoaHoc().getMaKhoaHoc());
            if (!khoaHocOptional.isPresent()) {
                return buildResponse(false, HttpStatus.NOT_FOUND,
                        messageSource.getMessage("khoahoc.notfound", null, "Course not found", null), null);
            }
            KhoaHoc khoaHoc = khoaHocOptional.get();
            Optional<NguoiDung> nguoiDung = nguoiDungRepository.findById(khoaHoc.getGiangVien().getMaNguoiDung());
            if (!nguoiDung.isPresent() || (nguoiDung.get().getVaiTro() != VaiTro.GIANG_VIEN && nguoiDung.get().getVaiTro() != VaiTro.QUAN_TRI)) {
                return buildResponse(false, HttpStatus.FORBIDDEN,
                        messageSource.getMessage("tailieu.update.unauthorized", null, "Unauthorized to update document", null), null);
            }
            TaiLieu updatedTaiLieu = taiLieuMapper.taiLieuDtoToTaiLieu(taiLieuDto);
            updatedTaiLieu.setMaTaiLieu(id);
            updatedTaiLieu.setBaiHoc(baiHoc);
            taiLieuRepository.save(updatedTaiLieu);
            TaiLieuDto responseDto = taiLieuMapper.taiLieuToTaiLieuDto(updatedTaiLieu);
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("tailieu.update.success", null, "Document updated successfully", null), responseDto);
        } catch (TaiLieuNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    messageSource.getMessage("tailieu.update.failure", null, "Failed to update document", null), e);
        }
    }

    public ApiResponse<?> deleteTaiLieu(Long id) {
        try {
            Optional<TaiLieu> taiLieu = taiLieuRepository.findById(id);
            if (!taiLieu.isPresent()) {
                throw new TaiLieuNotFoundException(
                        messageSource.getMessage("tailieu.notfound", null, "Document not found", null));
            }
            Optional<BaiHoc> baiHocOptional = baiHocRepository.findById(taiLieu.get().getBaiHoc().getMaBaiHoc());
            if (!baiHocOptional.isPresent()) {
                return buildResponse(false, HttpStatus.NOT_FOUND,
                        messageSource.getMessage("baihoc.notfound", null, "Lesson not found", null), null);
            }
            BaiHoc baiHoc = baiHocOptional.get();
            Optional<KhoaHoc> khoaHocOptional = khoaHocRepository.findById(baiHoc.getKhoaHoc().getMaKhoaHoc());
            if (!khoaHocOptional.isPresent()) {
                return buildResponse(false, HttpStatus.NOT_FOUND,
                        messageSource.getMessage("khoahoc.notfound", null, "Course not found", null), null);
            }
            KhoaHoc khoaHoc = khoaHocOptional.get();
            Optional<NguoiDung> nguoiDung = nguoiDungRepository.findById(khoaHoc.getGiangVien().getMaNguoiDung());
            if (!nguoiDung.isPresent() || (nguoiDung.get().getVaiTro() != VaiTro.GIANG_VIEN && nguoiDung.get().getVaiTro() != VaiTro.QUAN_TRI)) {
                return buildResponse(false, HttpStatus.FORBIDDEN,
                        messageSource.getMessage("tailieu.delete.unauthorized", null, "Unauthorized to delete document", null), null);
            }
            String absolutePath = uploadDirBase + taiLieu.get().getDuongDan().substring(1);
            Files.deleteIfExists(Paths.get(absolutePath));
            taiLieuRepository.delete(taiLieu.get());
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("tailieu.delete.success", null, "Document deleted successfully", null), null);
        } catch (IOException e) {
            throw new RuntimeException(
                    messageSource.getMessage("tailieu.delete.failure", null, "Failed to delete document", null), e);
        } catch (TaiLieuNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    messageSource.getMessage("tailieu.delete.failure", null, "Failed to delete document", null), e);
        }
    }

    public ApiResponse<TaiLieuDto> uploadTaiLieu(MultipartFile file, TaiLieuDto taiLieuDto) {
        try {
            if (file == null || file.isEmpty()) {
                return buildResponse(false, HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("tailieu.upload.file.empty", null, "File must not be empty", null), null);
            }
            long maxSizeInBytes = convertToBytes(fileStorageProperties.getMaxFileSize());
            if (file.getSize() > maxSizeInBytes) {
                return buildResponse(false, HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("tailieu.upload.file.too.large", new Object[]{fileStorageProperties.getMaxFileSize()}, "File size exceeds the maximum limit", null), null);
            }
            String contentType = file.getContentType();
            if (!isValidFileType(contentType, taiLieuDto.getLoaiFile())) {
                return buildResponse(false, HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("tailieu.upload.file.invalid", null, "Invalid file format", null), null);
            }
            Optional<BaiHoc> baiHocOptional = baiHocRepository.findById(taiLieuDto.getMaBaiHoc());
            if (!baiHocOptional.isPresent()) {
                return buildResponse(false, HttpStatus.NOT_FOUND,
                        messageSource.getMessage("baihoc.notfound", null, "Lesson not found", null), null);
            }
            BaiHoc baiHoc = baiHocOptional.get();
            Optional<KhoaHoc> khoaHocOptional = khoaHocRepository.findById(baiHoc.getKhoaHoc().getMaKhoaHoc());
            if (!khoaHocOptional.isPresent()) {
                return buildResponse(false, HttpStatus.NOT_FOUND,
                        messageSource.getMessage("khoahoc.notfound", null, "Course not found", null), null);
            }
            KhoaHoc khoaHoc = khoaHocOptional.get();
            Optional<NguoiDung> nguoiDung = nguoiDungRepository.findById(khoaHoc.getGiangVien().getMaNguoiDung());
            if (!nguoiDung.isPresent() || (nguoiDung.get().getVaiTro() != VaiTro.GIANG_VIEN && nguoiDung.get().getVaiTro() != VaiTro.QUAN_TRI)) {
                return buildResponse(false, HttpStatus.FORBIDDEN,
                        messageSource.getMessage("tailieu.create.unauthorized", null, "Unauthorized to create document", null), null);
            }
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                throw new IllegalArgumentException("Tên file không hợp lệ");
            }
            LoaiTaiLieu loai = getLoaiTaiLieuFromExtension(originalFilename);
            System.out.println("File name: " + file.getOriginalFilename());
            System.out.println("Size: " + file.getSize());
            String url = loai.toString().toLowerCase();
            String uploadDir = uploadDirBase + url + "/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String fileName = UUID.randomUUID() + "_" + originalFilename;
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            taiLieuDto.setTenFile(originalFilename);
            taiLieuDto.setDuongDan("/uploads/" + url + "/" + fileName);
            taiLieuDto.setKichThuoc((int) file.getSize());
            TaiLieu taiLieu = taiLieuMapper.taiLieuDtoToTaiLieu(taiLieuDto);
            taiLieu.setBaiHoc(baiHoc);
            taiLieuRepository.save(taiLieu);
            TaiLieuDto responseDto = taiLieuMapper.taiLieuToTaiLieuDto(taiLieu);
            return buildResponse(true, HttpStatus.CREATED,
                    messageSource.getMessage("tailieu.upload.success", null, "Document uploaded successfully", null), responseDto);
        } catch (IOException e) {
            throw new RuntimeException(
                    messageSource.getMessage("tailieu.upload.failure", null, "Failed to upload document", null), e);
        }
    }

    public Resource getDownloadFile(Long id) {
        try {
            Optional<TaiLieu> taiLieu = taiLieuRepository.findById(id);
            if (!taiLieu.isPresent()) {
                throw new TaiLieuNotFoundException(
                        messageSource.getMessage("tailieu.notfound", null, "Document not found", null));
            }
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            NguoiDung currentUser = nguoiDungRepository.findByTenDangNhap(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            TaiLieu taiLieuEntity = taiLieu.get();
            BaiHoc baiHoc = taiLieuEntity.getBaiHoc();
            KhoaHoc khoaHoc = baiHoc.getKhoaHoc();
            if (currentUser.getVaiTro() == VaiTro.HOC_VIEN) {
                boolean isEnrolled = dangKyKhoaHocRepository.existsByNguoiDungMaNguoiDungAndKhoaHocMaKhoaHocAndTrangThaiThanhToan(
                        currentUser.getMaNguoiDung(), khoaHoc.getMaKhoaHoc(), TrangThaiThanhToan.HOAN_THANH);
                if (!isEnrolled) {
                    throw new RuntimeException(
                            messageSource.getMessage("tailieu.download.unauthorized", null, "Unauthorized to download document", null));
                }
            }
            String absolutePath = uploadDirBase + taiLieuEntity.getDuongDan().substring(1);
            Path filePath = Paths.get(absolutePath);
            if (!Files.exists(filePath)) {
                throw new RuntimeException(
                        messageSource.getMessage("tailieu.get.failure", null, "Failed to retrieve document", null));
            }
            return new FileSystemResource(filePath);
        } catch (TaiLieuNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    messageSource.getMessage("tailieu.get.failure", null, "Failed to retrieve document", null), e);
        }
    }

    private boolean isValidFileType(String contentType, String loaiFile) {
        if (contentType == null) return false;
        switch (loaiFile.toUpperCase()) {
            case "VIDEO":
                return contentType.startsWith("video/");
            case "PDF":
                return contentType.equals("application/pdf");
            case "IMAGE":
                return contentType.startsWith("image/");
            case "CODE":
                return contentType.startsWith("text/") || contentType.equals("application/octet-stream");
        }
        return false;
    }

    private <T> ApiResponse<T> buildResponse(boolean isSuccess, HttpStatus status, String message, T data) {
        return ApiResponse.<T>builder()
                .success(isSuccess)
                .statusCode(status.value())
                .message(message)
                .data(data)
                .build();
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
        return Long.parseLong(maxFileSize.trim()); // bytes
    }

    private LoaiTaiLieu getLoaiTaiLieuFromExtension(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        switch (extension) {
            case "pdf":
                return LoaiTaiLieu.PDF;
            case "mp4":
            case "avi":
            case "mov":
            case "mkv":
                return LoaiTaiLieu.VIDEO;
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "bmp":
                return LoaiTaiLieu.IMAGE;
            case "java":
            case "py":
            case "js":
            case "cpp":
            case "html":
            case "css":
                return LoaiTaiLieu.CODE;
        }
        return null;
    }

}