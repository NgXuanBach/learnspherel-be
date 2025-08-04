package com.learnspherel.repository;

import com.learnspherel.entity.ThongBao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThongBaoRepository extends JpaRepository<ThongBao, Long> {
    List<ThongBao> findByNguoiDung_MaNguoiDung(Long maNguoiDung);
}