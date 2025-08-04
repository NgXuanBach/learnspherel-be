package com.learnspherel.service;

import com.learnspherel.dto.ReviewCourseRequestDTO;
import com.learnspherel.dto.ReviewCourseResponseDTO;
import com.learnspherel.entity.DanhGiaKhoaHoc;
import com.learnspherel.entity.KhoaHoc;
import com.learnspherel.entity.NguoiDung;
import com.learnspherel.exception.KhoaHocNotFoundException;
import com.learnspherel.exception.ReviewDuplicateException;
import com.learnspherel.exception.UsernameNotFoundException;
import com.learnspherel.mapper.DanhGiaKhoaHocMapper;
import com.learnspherel.repository.DanhGiaKhoaHocRepository;
import com.learnspherel.repository.KhoaHocRepository;
import com.learnspherel.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class DanhGiaKhoaHocService {
    private final DanhGiaKhoaHocRepository repo;
    private final KhoaHocRepository khoaHocRepo;
    private final NguoiDungRepository nguoiDungRepo;
    private final MessageSource messageSource;
    private final DanhGiaKhoaHocMapper mapper;

    public void reviewCourse(ReviewCourseRequestDTO req, Locale locale) {
        if (repo.existsByKhoaHoc_MaKhoaHocAndNguoiDung_MaNguoiDung(req.getMaKhoaHoc(), req.getMaNguoiDung())) {
            throw new ReviewDuplicateException(messageSource.getMessage("review.duplicate", null, locale));
        }
        KhoaHoc khoaHoc = khoaHocRepo.findById(req.getMaKhoaHoc())
                .orElseThrow(() -> new KhoaHocNotFoundException(messageSource.getMessage("course.notfound", null, locale)));
        NguoiDung nguoiDung = nguoiDungRepo.findById(req.getMaNguoiDung())
                .orElseThrow(() -> new UsernameNotFoundException(messageSource.getMessage("user.notfound", null, locale)));

        DanhGiaKhoaHoc entity = DanhGiaKhoaHoc.builder()
                .khoaHoc(khoaHoc)
                .nguoiDung(nguoiDung)
                .diemSo(req.getDiemSo())
                .binhLuan(req.getBinhLuan())
                .build();
        repo.save(entity);
    }

    public List<ReviewCourseResponseDTO> getAllByKhoaHoc(Long maKhoaHoc) {
        return repo.findAllByKhoaHoc_MaKhoaHoc(maKhoaHoc)
                .stream().map(mapper::toDTO).toList();
    }
}
