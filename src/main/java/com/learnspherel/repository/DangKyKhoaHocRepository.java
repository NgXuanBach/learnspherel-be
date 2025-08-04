package com.learnspherel.repository;

import com.learnspherel.dto.AdminRevenueReportDto;
import com.learnspherel.entity.DangKyKhoaHoc;
import com.learnspherel.entity.KhoaHoc;
import com.learnspherel.entity.enums.TrangThaiThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface DangKyKhoaHocRepository extends JpaRepository<DangKyKhoaHoc, Long> {
    boolean existsByNguoiDungMaNguoiDungAndKhoaHocMaKhoaHoc(Long maNguoiDung, Long maKhoaHoc);

    List<DangKyKhoaHoc> findByNguoiDungMaNguoiDungAndKhoaHocMaKhoaHoc(Long maNguoiDung, Long maKhoaHoc);

    boolean existsByNguoiDungMaNguoiDungAndKhoaHocMaKhoaHocAndTrangThaiThanhToan(Long maNguoiDung, Long khoaHoc, TrangThaiThanhToan trangThaiToan);

    List<DangKyKhoaHoc> findByNguoiDungMaNguoiDung(Long nguoiDungMaNguoiDung);

    List<DangKyKhoaHoc> findByNguoiDungMaNguoiDungAndTrangThaiThanhToan(Long nguoiDungMaNguoiDung, TrangThaiThanhToan trangThaiThanhToan);

    List<DangKyKhoaHoc> findByKhoaHocInAndTrangThaiThanhToan(Collection<KhoaHoc> khoaHocs, TrangThaiThanhToan trangThaiThanhToan);

    @Query("SELECT new com.learnspherel.dto.AdminRevenueReportDto(" +
            "MONTH(dk.ngayDangKy), YEAR(dk.ngayDangKy), kh.maKhoaHoc, kh.tieuDe, " +
            "COUNT(dk), SUM(kh.gia), SUM(kh.gia) * 0.3) " +
            "FROM DangKyKhoaHoc dk JOIN dk.khoaHoc kh " +
            "WHERE kh.trangThai = com.learnspherel.entity.enums.TrangThaiKhoaHoc.DANG_MO " +
            "AND kh.coPhi = TRUE " +
            "AND dk.trangThaiThanhToan = com.learnspherel.entity.enums.TrangThaiThanhToan.HOAN_THANH " +
            "AND (:month IS NULL OR MONTH(dk.ngayDangKy) = :month) " +
            "AND (:year IS NULL OR YEAR(dk.ngayDangKy) = :year) " +
            "GROUP BY YEAR(dk.ngayDangKy), MONTH(dk.ngayDangKy), kh.maKhoaHoc, kh.tieuDe")
    List<AdminRevenueReportDto> getAdminRevenueReport(
            @Param("month") Integer month,
            @Param("year") Integer year
    );
}