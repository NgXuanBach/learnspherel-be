package com.learnspherel.repository;

import com.learnspherel.entity.KhoaHoc;
import com.learnspherel.entity.NguoiDung;
import com.learnspherel.entity.YeuCauXoaKhoaHoc;
import com.learnspherel.entity.enums.TrangThaiYeuCau;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface YeuCauXoaKhoaHocRepository extends JpaRepository<YeuCauXoaKhoaHoc, Long> {
    boolean existsByKhoaHocAndTrangThai(KhoaHoc khoaHoc, TrangThaiYeuCau trangThai);
    List<YeuCauXoaKhoaHoc> findAllByTrangThai(TrangThaiYeuCau trangThai);

    List<YeuCauXoaKhoaHoc> findByGiangVienAndKhoaHoc(NguoiDung giangVien, KhoaHoc khoaHoc);

    List<YeuCauXoaKhoaHoc> findByGiangVienAndKhoaHocAndTrangThai(NguoiDung giangVien, KhoaHoc khoaHoc, TrangThaiYeuCau trangThai);
}
