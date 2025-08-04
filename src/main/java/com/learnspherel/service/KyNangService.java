package com.learnspherel.service;

import com.learnspherel.dto.ApiResponse;
import com.learnspherel.dto.KyNangRequest;
import com.learnspherel.dto.KyNangResponse;
import com.learnspherel.entity.KyNang;
import com.learnspherel.exception.KyNangAlreadyExistsException;
import com.learnspherel.mapper.KyNangMapper;
import com.learnspherel.repository.KyNangRepository;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KyNangService {

    private final KyNangRepository kyNangRepository;
    private final KyNangMapper kyNangMapper;
    private final MessageSource messageSource;

    public KyNangService(KyNangRepository kyNangRepository, KyNangMapper kyNangMapper, MessageSource messageSource) {
        this.kyNangRepository = kyNangRepository;
        this.kyNangMapper = kyNangMapper;
        this.messageSource = messageSource;
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<KyNangResponse>> getAllKyNang() {
        List<KyNangResponse> responses = kyNangRepository.findAll().stream()
                .map(kyNangMapper::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.<List<KyNangResponse>>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message(messageSource.getMessage("kynang.getall.success", null, "Lấy danh sách kỹ năng thành công", null))
                .data(responses)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<KyNangResponse>> searchKyNang(String keyword) {
        List<KyNangResponse> responses = kyNangRepository.findByTenKyNangContainingIgnoreCase(keyword).stream()
                .map(kyNangMapper::toResponse)
                .collect(Collectors.toList());
        String message = responses.isEmpty()
                ? messageSource.getMessage("kynang.search.empty", null, "Không tìm thấy kỹ năng nào", null)
                : messageSource.getMessage("kynang.search.success", null, "Tìm kiếm kỹ năng thành công", null);
        return ApiResponse.<List<KyNangResponse>>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message(message)
                .data(responses)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    @Transactional
    public ApiResponse<KyNangResponse> createKyNang(KyNangRequest request) {
        if (kyNangRepository.existsByTenKyNang(request.getTenKyNang())) {
            throw new KyNangAlreadyExistsException(
                    messageSource.getMessage("kynang.already.exists", null, "Skill already exists", null));
        }

        KyNang kyNang = kyNangMapper.toEntity(request);
        kyNang = kyNangRepository.save(kyNang);
        KyNangResponse response = kyNangMapper.toResponse(kyNang);

        return ApiResponse.<KyNangResponse>builder()
                .success(true)
                .statusCode(HttpStatus.CREATED.value())
                .message(messageSource.getMessage("kynang.create.success", null, "Thêm kỹ năng thành công", null))
                .data(response)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}