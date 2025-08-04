package com.learnspherel.repository;

import com.learnspherel.entity.KhoaHoc;
import com.learnspherel.entity.enums.TrangThaiKhoaHoc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KhoaHocRepository extends JpaRepository<KhoaHoc, Long> {

    List<KhoaHoc> findByTieuDeContainingIgnoreCase(String tieuDe);
    List<KhoaHoc> findByGiangVienMaNguoiDung(Long maGiangVien);
    // Lấy tất cả khoá học đang mở
    List<KhoaHoc> findAllByTrangThai(TrangThaiKhoaHoc trangThai);

    // Lấy khoá học đang mở theo giảng viên
    List<KhoaHoc> findAllByTrangThaiAndGiangVien_MaNguoiDung(TrangThaiKhoaHoc trangThai, Long maGiangVien);

    List<KhoaHoc> findByTrangThai(TrangThaiKhoaHoc trangThai);

    List<KhoaHoc> findAllByTrangThaiNotAndGiangVien_MaNguoiDung(TrangThaiKhoaHoc trangThai, Long giangVienMaNguoiDung);
}