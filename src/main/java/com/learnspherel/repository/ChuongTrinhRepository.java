package com.learnspherel.repository;

import com.learnspherel.entity.ChuongTrinh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChuongTrinhRepository extends JpaRepository<ChuongTrinh, Long> {
    List<ChuongTrinh> findByKhoaHocMaKhoaHoc(Long maKhoaHoc);
}