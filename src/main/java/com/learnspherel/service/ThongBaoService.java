package com.learnspherel.service;

import com.learnspherel.entity.ThongBao;
import com.learnspherel.repository.ThongBaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ThongBaoService {

    private final ThongBaoRepository thongBaoRepository;

    public ThongBao saveThongBao(ThongBao thongBao) {
        return thongBaoRepository.save(thongBao);
    }

    public List<ThongBao> findByNguoiDungId(Long maNguoiDung) {
        return thongBaoRepository.findByNguoiDung_MaNguoiDung(maNguoiDung);
    }

    public Optional<ThongBao> findById(Long id) {
        return thongBaoRepository.findById(id);
    }

    public List<ThongBao> findAll() {
        return thongBaoRepository.findAll();
    }

    public void deleteById(Long id) {
        thongBaoRepository.deleteById(id);
    }
}