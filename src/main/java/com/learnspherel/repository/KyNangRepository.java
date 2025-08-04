package com.learnspherel.repository;

import com.learnspherel.entity.KyNang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KyNangRepository extends JpaRepository<KyNang, Long> {

    List<KyNang> findByTenKyNangContainingIgnoreCase(String keyword);

    boolean existsByTenKyNang(String tenKyNang);
}