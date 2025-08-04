package com.learnspherel.repository;

import com.learnspherel.entity.DanhGiaGiangVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanhGiaGiangVienRepository extends JpaRepository<DanhGiaGiangVien, Long> {

    @Query("SELECT AVG(dg.diemSo), COUNT(dg) FROM DanhGiaGiangVien dg WHERE dg.giangVien.maNguoiDung = :giangVienId")
    List<Object[]> findAverageAndCountByGiangVienId(Long giangVienId);
    boolean existsByNguoiDung_MaNguoiDungAndGiangVien_MaNguoiDung(Long maNguoiDung, Long maGiangVien);
    List<DanhGiaGiangVien> findAllByGiangVien_MaNguoiDung(Long maGiangVien);
}