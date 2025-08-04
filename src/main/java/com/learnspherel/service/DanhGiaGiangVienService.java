package com.learnspherel.service;

import com.learnspherel.dto.ReviewTeacherRequestDTO;
import com.learnspherel.dto.ReviewTeacherResponseDTO;
import com.learnspherel.entity.DanhGiaGiangVien;
import com.learnspherel.entity.NguoiDung;
import com.learnspherel.exception.ReviewTeacherDuplicateException;
import com.learnspherel.exception.UsernameNotFoundException;
import com.learnspherel.repository.DanhGiaGiangVienRepository;
import com.learnspherel.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class DanhGiaGiangVienService {
    private final DanhGiaGiangVienRepository repo;
    private final NguoiDungRepository nguoiDungRepo;
    private final MessageSource messageSource;

    public void reviewTeacher(ReviewTeacherRequestDTO req, Locale locale) {
        if (repo.existsByNguoiDung_MaNguoiDungAndGiangVien_MaNguoiDung(req.getMaNguoiDung(), req.getMaGiangVien())) {
            throw new ReviewTeacherDuplicateException(
                    messageSource.getMessage("teacher.review.duplicate", null, locale)
            );
        }
        NguoiDung nguoiDung = nguoiDungRepo.findById(req.getMaNguoiDung())
                .orElseThrow(() -> new UsernameNotFoundException(messageSource.getMessage("user.notfound", null, locale)));
        NguoiDung giangVien = nguoiDungRepo.findById(req.getMaGiangVien())
                .orElseThrow(() -> new UsernameNotFoundException(messageSource.getMessage("teacher.notfound", null, locale)));
        DanhGiaGiangVien entity = DanhGiaGiangVien.builder()
                .nguoiDung(nguoiDung)
                .giangVien(giangVien)
                .diemSo(req.getDiemSo())
                .binhLuan(req.getBinhLuan())
                .build();
        repo.save(entity);
    }

    public List<ReviewTeacherResponseDTO> getAllByMaGiangVien(Long maGiangVien) {
        return repo.findAllByGiangVien_MaNguoiDung(maGiangVien)
                .stream()
                .map(e -> new ReviewTeacherResponseDTO(
                        e.getMaDanhGia(),
                        e.getNguoiDung().getMaNguoiDung(),
                        e.getNguoiDung().getTenNguoiDung(),
                        e.getDiemSo(),
                        e.getBinhLuan(),
                        e.getNgayTao()
                )).toList();
    }
}
