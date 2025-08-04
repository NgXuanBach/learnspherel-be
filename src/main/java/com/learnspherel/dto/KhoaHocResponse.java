package com.learnspherel.dto;

import com.learnspherel.entity.enums.ThoiHan;
import com.learnspherel.entity.enums.TrangThaiKhoaHoc;
import com.learnspherel.entity.enums.TrinhDo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhoaHocResponse {

    private Long maKhoaHoc;
    private String tieuDe;
    private String moTa;
    private String anhDaiDien;
    private TrinhDo trinhDo;
    private String maGiangVien;
    private String tenGiangVien;
    private String anhDaiDienGiangVien;
    private Boolean coPhi;
    private Double gia;
    private String ngonNgu;
    private ThoiHan thoiHan;
    private Boolean chungChi;
    private Double tongThoiLuong;
    private Integer soBaiGiang;
    private Integer soNguoiThamGia;
    private Double diemDanhGiaKhoaHoc;
    private Integer soDanhGiaKhoaHoc;
    private Double diemDanhGiaGiangVien;
    private Integer soDanhGiaGiangVien;
    private Integer soKhoaHocGiangVien;
    private Integer soHocVienGiangVien;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;
    private List<Long> kyNangIds;
    private String videoDemoUrl;
    private boolean coYeuCauXoaDangCho;
    private TrangThaiKhoaHoc trangThai;
}