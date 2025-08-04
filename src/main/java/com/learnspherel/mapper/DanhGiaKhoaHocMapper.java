package com.learnspherel.mapper;

import com.learnspherel.dto.ReviewCourseResponseDTO;
import com.learnspherel.entity.DanhGiaKhoaHoc;
import org.springframework.stereotype.Component;

@Component
public class DanhGiaKhoaHocMapper {
    public ReviewCourseResponseDTO toDTO(DanhGiaKhoaHoc e) {
        return new ReviewCourseResponseDTO(
                e.getMaDanhGia(),
                e.getNguoiDung() != null ? e.getNguoiDung().getMaNguoiDung() : null,
                e.getNguoiDung() != null ? e.getNguoiDung().getTenNguoiDung() : null,
                e.getDiemSo(),
                e.getBinhLuan(),
                e.getNgayTao()
        );
    }
}
