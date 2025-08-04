package com.learnspherel.service;

import com.learnspherel.dto.ApiResponse;
import com.learnspherel.dto.TienDoHocTapDto;
import com.learnspherel.entity.BaiHoc;
import com.learnspherel.entity.NguoiDung;
import com.learnspherel.entity.TienDoHocTap;
import com.learnspherel.entity.enums.TrangThaiHocTap;
import com.learnspherel.exception.BaiHocNotFoundException;
import com.learnspherel.repository.BaiHocRepository;
import com.learnspherel.repository.NguoiDungRepository;
import com.learnspherel.repository.TienDoHocTapRepository;
import com.learnspherel.utils.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TienDoHocTapService {

    private final TienDoHocTapRepository tienDoHocTapRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final BaiHocRepository baiHocRepository;
    private final JwtUtil jwtUtil;
    private final MessageSource messageSource;



    public ApiResponse<Void> updateTienDoHocTap(TienDoHocTapDto request, String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            NguoiDung user = nguoiDungRepository.findByUsernameOrEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Người dùng không tồn tại"));

            BaiHoc baiHoc = baiHocRepository.findById(request.getMaBaiHoc())
                    .orElseThrow(() -> new BaiHocNotFoundException("Bài học không tồn tại"));

            // Kiểm tra xem đã có tiến độ cho bài học này chưa
            List<TienDoHocTap> existingTienDo = tienDoHocTapRepository.findByNguoiDungMaNguoiDungAndBaiHocMaBaiHoc(
                    user.getMaNguoiDung(), baiHoc.getMaBaiHoc());
            TienDoHocTap tienDo;
            if (existingTienDo.isEmpty()) {
                tienDo = TienDoHocTap.builder()
                        .nguoiDung(user)
                        .khoaHoc(baiHoc.getKhoaHoc())
                        .baiHoc(baiHoc)
                        .trangThai(TrangThaiHocTap.valueOf(request.getTrangThai()))
                        .ngayHoanThanh(request.getTrangThai().equals("HOAN_THANH") ? LocalDateTime.now() : null)
                        .build();
            } else {
                tienDo = existingTienDo.get(0);
                tienDo.setTrangThai(TrangThaiHocTap.valueOf(request.getTrangThai()));
                tienDo.setNgayHoanThanh(request.getTrangThai().equals("HOAN_THANH") ? LocalDateTime.now() : null);
            }

            tienDoHocTapRepository.save(tienDo);
            return buildResponse(true, HttpStatus.OK,
                    messageSource.getMessage("tiendo.update.success", null,
                            "Cập nhật tiến độ học tập thành công", null), null);
        } catch (UsernameNotFoundException | BaiHocNotFoundException e) {
            return buildResponse(false, HttpStatus.NOT_FOUND,
                    messageSource.getMessage("tiendo.update.failure", null,
                            "Cập nhật tiến độ học tập thất bại", null), null);
        }
//        catch (Exception e) {
//            return buildResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
//                    messageSource.getMessage("tiendo.update.failure", null,
//                            "Cập nhật tiến độ học tập thất bại", null), null);
//        }
    }

    public ApiResponse<Boolean> checkHoanThanKhoaHoc(long khoaHocId, String token) {
        String username = jwtUtil.extractUsername(token);
        NguoiDung user = nguoiDungRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Người dùng không tồn tại"));
        List<TienDoHocTap> tienDoHocTapList = tienDoHocTapRepository.findByNguoiDungMaNguoiDungAndKhoaHocMaKhoaHoc(user.getMaNguoiDung(), khoaHocId);
        List<BaiHoc> baiHocList = baiHocRepository.findByKhoaHocMaKhoaHoc(khoaHocId);
        if(baiHocList.size() != tienDoHocTapList.size()) {
            return buildResponse(true, HttpStatus.OK, "Người dùng chưa hoàn thành khoá học này", false);
        }
        if (tienDoHocTapList.isEmpty()) {
            return buildResponse(false, HttpStatus.NOT_FOUND, "Người dùng chưa học khoá học này", false);
        }
        for (TienDoHocTap tienDoHocTap : tienDoHocTapList) {
            if (!tienDoHocTap.getTrangThai().equals(TrangThaiHocTap.HOAN_THANH))
                return buildResponse(true, HttpStatus.OK, "Người dùng chưa hoàn thành khoá học này", false);
        }
        return buildResponse(true, HttpStatus.OK, "Người dùng đã hoàn thành khoá học", true);

    }

    private <T> ApiResponse<T> buildResponse(boolean isSuccess, HttpStatus status, String message, T data) {
        return ApiResponse.<T>builder()
                .success(isSuccess)
                .statusCode(status.value())
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}