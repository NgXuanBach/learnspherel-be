package com.learnspherel.service;

import com.learnspherel.entity.BaiNop;
import com.learnspherel.repository.BaiNopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BaiNopService {

    private final BaiNopRepository baiNopRepository;

    public BaiNop saveBaiNop(BaiNop baiNop) {
        return baiNopRepository.save(baiNop);
    }

    public List<BaiNop> findByNguoiDungId(Long maNguoiDung) {
        return baiNopRepository.findByNguoiDung_MaNguoiDung(maNguoiDung);
    }

    public List<BaiNop> findByBaiKiemTraId(Long maBaiKiemTra) {
        return baiNopRepository.findByBaiKiemTra_MaBaiKiemTra(maBaiKiemTra);
    }

    public Optional<BaiNop> findById(Long id) {
        return baiNopRepository.findById(id);
    }

    public List<BaiNop> findAll() {
        return baiNopRepository.findAll();
    }

    public void deleteById(Long id) {
        baiNopRepository.deleteById(id);
    }
}