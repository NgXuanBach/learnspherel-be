package com.learnspherel.service;

import com.learnspherel.entity.CauHoi;
import com.learnspherel.repository.CauHoiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CauHoiService {

    private final CauHoiRepository cauHoiRepository;

    public CauHoi saveCauHoi(CauHoi cauHoi) {
        return cauHoiRepository.save(cauHoi);
    }

    public List<CauHoi> findByBaiKiemTraId(Long maBaiKiemTra) {
        return cauHoiRepository.findByBaiKiemTra_MaBaiKiemTra(maBaiKiemTra);
    }

    public Optional<CauHoi> findById(Long id) {
        return cauHoiRepository.findById(id);
    }

    public List<CauHoi> findAll() {
        return cauHoiRepository.findAll();
    }

    public void deleteById(Long id) {
        cauHoiRepository.deleteById(id);
    }
}