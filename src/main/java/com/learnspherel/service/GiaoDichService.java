package com.learnspherel.service;

import com.learnspherel.entity.GiaoDich;
import com.learnspherel.repository.GiaoDichRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GiaoDichService {

    private final GiaoDichRepository giaoDichRepository;

    public GiaoDich saveGiaoDich(GiaoDich giaoDich) {
        return giaoDichRepository.save(giaoDich);
    }

    public Optional<GiaoDich> findById(Long id) {
        return giaoDichRepository.findById(id);
    }

    public List<GiaoDich> findAll() {
        return giaoDichRepository.findAll();
    }

    public void deleteById(Long id) {
        giaoDichRepository.deleteById(id);
    }
}