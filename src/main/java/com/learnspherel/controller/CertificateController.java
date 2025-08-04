package com.learnspherel.controller;

import com.learnspherel.dto.CertificateRequestDTO;
import com.learnspherel.dto.CertificateResponseDTO;
import com.learnspherel.service.CertificateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @PostMapping
    public ResponseEntity<CertificateResponseDTO> issueCertificate(
            @RequestBody @Valid CertificateRequestDTO dto, Locale locale) {
        return ResponseEntity.ok(certificateService.issueCertificate(dto, locale));
    }

    @GetMapping("/{code}")
    public ResponseEntity<CertificateResponseDTO> getByCode(@PathVariable String code, Locale locale) {
        return ResponseEntity.ok(certificateService.getByCode(code, locale));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CertificateResponseDTO>> getUserCertificates(@PathVariable Long userId) {
        return ResponseEntity.ok(certificateService.getUserCertificates(userId));
    }

    @GetMapping("/user/{userId}/course/{courseId}")
    public ResponseEntity<CertificateResponseDTO> getByUserAndCourse(
            @PathVariable Long userId,
            @PathVariable Long courseId,
            Locale locale) {
        return ResponseEntity.ok(
                certificateService.getByUserAndCourse(userId, courseId, locale)
        );
    }
}
