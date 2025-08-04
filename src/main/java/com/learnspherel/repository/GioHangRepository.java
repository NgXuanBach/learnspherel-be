package com.learnspherel.repository;

import com.learnspherel.entity.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Long> {
    List<GioHang> findByNguoiDungMaNguoiDung(Long maNguoiDung);
    boolean existsByNguoiDungMaNguoiDungAndKhoaHocMaKhoaHoc(Long maNguoiDung, Long maKhoaHoc);
    void deleteByNguoiDungMaNguoiDung(Long maNguoiDung);

    void deleteByNguoiDungMaNguoiDungAndKhoaHocMaKhoaHoc(Long nguoiDungMaNguoiDung, Long khoaHocMaKhoaHoc);

    void deleteByNguoiDungMaNguoiDungAndKhoaHocMaKhoaHocIn(Long nguoiDungMaNguoiDung, Collection<Long> khoaHocMaKhoaHocs);
}