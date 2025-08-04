package com.learnspherel.repository;

import com.learnspherel.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {
    Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);

    boolean existsByTenDangNhap(String tenDangNhap);

    boolean existsByEmail(String email);

    @Query("SELECT n FROM NguoiDung n WHERE n.tenDangNhap = ?1 OR n.email = ?1")
    Optional<NguoiDung> findByUsernameOrEmail(String searchParam);

    Optional<NguoiDung> findByTenDangNhapOrEmail(String tenDangNhap, String email);

    @Query("SELECT n FROM NguoiDung n WHERE n.trangThai = 'HOAT_DONG'")
    List<NguoiDung> findAllActive();
}