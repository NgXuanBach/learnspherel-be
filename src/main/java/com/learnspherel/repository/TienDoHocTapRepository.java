package com.learnspherel.repository;

import com.learnspherel.entity.TienDoHocTap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TienDoHocTapRepository extends JpaRepository<TienDoHocTap, Long> {
    List<TienDoHocTap> findByNguoiDung_MaNguoiDung(Long maNguoiDung);
    List<TienDoHocTap> findByKhoaHoc_MaKhoaHoc(Long maKhoaHoc);

    List<TienDoHocTap> findByNguoiDungMaNguoiDungAndKhoaHocMaKhoaHoc(Long nguoiDungMaNguoiDung, Long khoaHocMaKhoaHoc);

    List<TienDoHocTap> findByNguoiDungMaNguoiDungAndBaiHocMaBaiHoc(Long nguoiDungMaNguoiDung, Long baiHocMaBaiHoc);

    List<TienDoHocTap> findByKhoaHocMaKhoaHoc(Long khoaHocMaKhoaHoc);
}