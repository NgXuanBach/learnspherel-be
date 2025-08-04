package com.learnspherel.service;

import com.learnspherel.entity.PhienDangNhap;
import com.learnspherel.repository.PhienDangNhapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhienDangNhapService {

    private final PhienDangNhapRepository phienDangNhapRepository;

    public PhienDangNhap savePhienDangNhap(PhienDangNhap phienDangNhap) {
        return phienDangNhapRepository.save(phienDangNhap);
    }

    public Optional<PhienDangNhap> findById(Long id) {
        return phienDangNhapRepository.findById(id);
    }

    public List<PhienDangNhap> findAll() {
        return phienDangNhapRepository.findAll();
    }

    public void deleteById(Long id) {
        phienDangNhapRepository.deleteById(id);
    }
}