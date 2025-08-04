package com.learnspherel.repository;

import com.learnspherel.entity.BaiKiemTra;
import com.learnspherel.entity.enums.LoaiBaiKiemTra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BaiKiemTraRepository extends JpaRepository<BaiKiemTra, Long> {
    List<BaiKiemTra> findByKhoaHoc_MaKhoaHoc(Long maKhoaHoc);
    List<BaiKiemTra> findByKhoaHocMaKhoaHoc(Long maKhoaHoc);

    Optional<BaiKiemTra> findByMaBaiKiemTraAndKhoaHocMaKhoaHoc(Long maBaiKiemTra, Long maKhoaHoc);

    List<BaiKiemTra> findByKhoaHocMaKhoaHocAndLoai(Long maKhoaHoc, LoaiBaiKiemTra loai);
}