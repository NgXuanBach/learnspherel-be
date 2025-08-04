package com.learnspherel.service;

import com.learnspherel.dto.CertificateRequestDTO;
import com.learnspherel.dto.CertificateResponseDTO;
import com.learnspherel.entity.Certificate;
import com.learnspherel.entity.KhoaHoc;
import com.learnspherel.entity.NguoiDung;
import com.learnspherel.entity.enums.CertificateStatus;
import com.learnspherel.exception.CertificateAlreadyExistsException;
import com.learnspherel.exception.CertificateNotFoundException;
import com.learnspherel.exception.KhoaHocNotFoundException;
import com.learnspherel.exception.UsernameNotFoundException;
import com.learnspherel.mapper.CertificateMapper;
import com.learnspherel.repository.CertificateRepository;
import com.learnspherel.repository.KhoaHocRepository;
import com.learnspherel.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final KhoaHocRepository khoaHocRepository;
    private final CertificateMapper certificateMapper;
    private final MessageSource messageSource;

    public CertificateResponseDTO issueCertificate(CertificateRequestDTO req, Locale locale) {
        // Kiểm tra user và course
        NguoiDung user = nguoiDungRepository.findById(req.getMaNguoiDung())
                .orElseThrow(() -> new UsernameNotFoundException(messageSource.getMessage("user.notfound", null, locale)));
        KhoaHoc course = khoaHocRepository.findById(req.getMaKhoaHoc())
                .orElseThrow(() -> new KhoaHocNotFoundException(messageSource.getMessage("course.notfound", null, locale)));

        // KIỂM TRA ĐÃ CÓ CHỨNG CHỈ CHƯA?
        boolean exists = certificateRepository
                .findByNguoiDung_MaNguoiDungAndKhoaHoc_MaKhoaHoc(req.getMaNguoiDung(), req.getMaKhoaHoc())
                .isPresent();

        if (exists) {
            throw new CertificateAlreadyExistsException(
                    messageSource.getMessage("certificate.already.exists", null, locale)
            );
        }
        // Nếu chưa có chứng chỉ thì cấp mới
        Certificate entity = certificateMapper.toEntity(req);
        entity.setNguoiDung(user);
        entity.setKhoaHoc(course);
        entity.setTrangThai(CertificateStatus.HIEU_LUC);
        entity.setNgayCap(LocalDateTime.now());
        entity.setNguoiKy(course.getGiangVien().getTenNguoiDung());
        entity.setMaChungChiXacNhan(generateCertCode());
        Certificate saved = certificateRepository.save(entity);
        return certificateMapper.toResponseDto(saved);
    }


    public CertificateResponseDTO getByCode(String code, Locale locale) {
        Certificate cert = certificateRepository.findByMaChungChiXacNhan(code)
                .orElseThrow(() -> new CertificateNotFoundException(messageSource.getMessage("certificate.notfound", null, locale)));
        return certificateMapper.toResponseDto(cert);
    }

    public List<CertificateResponseDTO> getUserCertificates(Long maNguoiDung) {
        return certificateRepository.findByNguoiDung_MaNguoiDung(maNguoiDung)
                .stream().map(certificateMapper::toResponseDto).toList();
    }

    public CertificateResponseDTO getByUserAndCourse(Long userId, Long courseId, Locale locale) {
        Certificate cert = certificateRepository
                .findByNguoiDung_MaNguoiDungAndKhoaHoc_MaKhoaHoc(userId, courseId)
                .orElseThrow(() -> new CertificateNotFoundException(
                        messageSource.getMessage("certificate.notfound", null, locale)
                ));
        return certificateMapper.toResponseDto(cert);
    }

    // Generate code, ex: LS202407190001ABC
    private String generateCertCode() {
        return "LS" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())
                + RandomStringUtils.randomAlphanumeric(4).toUpperCase();
    }
}
