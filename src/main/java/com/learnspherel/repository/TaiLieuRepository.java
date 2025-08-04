package com.learnspherel.repository;

import com.learnspherel.entity.TaiLieu;
import com.learnspherel.entity.enums.LoaiTaiLieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaiLieuRepository extends JpaRepository<TaiLieu, Long> {
    List<TaiLieu> findByBaiHocMaBaiHocAndLoaiFile(Long baiHocMaBaiHoc, LoaiTaiLieu loaiFile);

    List<TaiLieu> findByBaiHocMaBaiHocAndBaiNopIsNullAndLoaiFile(Long maBaiHoc, LoaiTaiLieu loaiFile);

    List<TaiLieu> findByBaiHocMaBaiHocAndBaiNopIsNull(Long maBaiHoc);

    List<TaiLieu> findByBaiHocMaBaiHoc(Long baiHocMaBaiHoc);
}