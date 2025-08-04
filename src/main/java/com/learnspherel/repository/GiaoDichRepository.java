package com.learnspherel.repository;

import com.learnspherel.entity.GiaoDich;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiaoDichRepository extends JpaRepository<GiaoDich, Long> {
    List<GiaoDich> findByMaGiaoDichNganHang(String maGiaoDichNganHang);

    List<GiaoDich> findByDangKyMaDangKy(Long dangKyMaDangKy);

    List<GiaoDich> findByPaypalToken(String paypalToken); // Thêm phương thức mới
}