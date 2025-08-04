package com.learnspherel.repository;

import com.learnspherel.entity.BaiNop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BaiNopRepository extends JpaRepository<BaiNop, Long> {
    List<BaiNop> findByNguoiDung_MaNguoiDung(Long maNguoiDung);
    List<BaiNop> findByBaiKiemTra_MaBaiKiemTra(Long maBaiKiemTra);
}