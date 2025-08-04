package com.learnspherel.repository;

import com.learnspherel.entity.DanhGiaKhoaHoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanhGiaKhoaHocRepository extends JpaRepository<DanhGiaKhoaHoc, Long> {

    @Query("SELECT AVG(dg.diemSo), COUNT(dg) FROM DanhGiaKhoaHoc dg WHERE dg.khoaHoc.maKhoaHoc = :khoaHocId")
    List<Object[]> findAverageAndCountByKhoaHocId(Long khoaHocId);

    boolean existsByKhoaHoc_MaKhoaHocAndNguoiDung_MaNguoiDung(Long maKhoaHoc, Long maNguoiDung);
    List<DanhGiaKhoaHoc> findAllByKhoaHoc_MaKhoaHoc(Long maKhoaHoc);

}