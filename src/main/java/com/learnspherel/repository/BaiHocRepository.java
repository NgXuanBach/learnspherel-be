package com.learnspherel.repository;

import com.learnspherel.entity.BaiHoc;
import com.learnspherel.entity.ChuongTrinh;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BaiHocRepository extends JpaRepository<BaiHoc, Long> {
    @EntityGraph(attributePaths = {"khoaHoc"})
    List<BaiHoc> findByKhoaHocMaKhoaHoc(Long maKhoaHoc);

    List<BaiHoc> findByChuongTrinhMaChuongTrinh(Long chuongTrinhMaChuongTrinh);

    void deleteAllByChuongTrinh(ChuongTrinh chuongTrinh);

    List<BaiHoc> findAllByChuongTrinh_MaChuongTrinhOrderByThuTuBaiHocAsc(Long chuongTrinhMaChuongTrinh);
}