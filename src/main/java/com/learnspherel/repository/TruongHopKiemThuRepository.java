package com.learnspherel.repository;

import com.learnspherel.entity.TruongHopKiemThu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TruongHopKiemThuRepository extends JpaRepository<TruongHopKiemThu, Long> {
    List<TruongHopKiemThu> findByBaiKiemTra_MaBaiKiemTra(Long maBaiKiemTra);
}