package com.learnspherel.service;

import com.learnspherel.entity.TruongHopKiemThu;
import com.learnspherel.repository.TruongHopKiemThuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TruongHopKiemThuService {

    private final TruongHopKiemThuRepository truongHopKiemThuRepository;

    public TruongHopKiemThu saveTruongHopKiemThu(TruongHopKiemThu truongHopKiemThu) {
        return truongHopKiemThuRepository.save(truongHopKiemThu);
    }

    public List<TruongHopKiemThu> findByBaiKiemTraId(Long maBaiKiemTra) {
        return truongHopKiemThuRepository.findByBaiKiemTra_MaBaiKiemTra(maBaiKiemTra);
    }

    public Optional<TruongHopKiemThu> findById(Long id) {
        return truongHopKiemThuRepository.findById(id);
    }

    public List<TruongHopKiemThu> findAll() {
        return truongHopKiemThuRepository.findAll();
    }

    public void deleteById(Long id) {
        truongHopKiemThuRepository.deleteById(id);
    }
}