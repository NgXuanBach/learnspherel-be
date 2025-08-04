package com.learnspherel.repository;

import com.learnspherel.entity.CauHoi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CauHoiRepository extends JpaRepository<CauHoi, Long> {
    List<CauHoi> findByBaiKiemTra_MaBaiKiemTra(Long maBaiKiemTra);
}