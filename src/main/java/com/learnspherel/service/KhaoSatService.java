package com.learnspherel.service;

import com.learnspherel.entity.KhaoSat;
import com.learnspherel.repository.KhaoSatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KhaoSatService {

    private final KhaoSatRepository khaoSatRepository;

    public KhaoSat saveKhaoSat(KhaoSat khaoSat) {
        return khaoSatRepository.save(khaoSat);
    }

    public Optional<KhaoSat> findById(Long id) {
        return khaoSatRepository.findById(id);
    }

    public List<KhaoSat> findAll() {
        return khaoSatRepository.findAll();
    }

    public void deleteById(Long id) {
        khaoSatRepository.deleteById(id);
    }
}