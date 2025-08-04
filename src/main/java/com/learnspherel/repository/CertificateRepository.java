package com.learnspherel.repository;

import com.learnspherel.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByMaChungChiXacNhan(String maChungChiXacNhan);
    List<Certificate> findByNguoiDung_MaNguoiDung(Long maNguoiDung);
    List<Certificate> findByKhoaHoc_MaKhoaHoc(Long maKhoaHoc);
    Optional<Certificate> findByNguoiDung_MaNguoiDungAndKhoaHoc_MaKhoaHoc(Long maNguoiDung, Long maKhoaHoc);

}
