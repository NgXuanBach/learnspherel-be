//package com.learnspherel.controller;
//
//import com.learnspherel.entity.BaiNop;
//import com.learnspherel.service.BaiNopService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api")
//public class BaiNopController {
//    @Autowired
//    private BaiNopService baiNopService;
//
//    @PostMapping("/bai-kiem-tra/{maBaiKiemTra}/nop-bai")
//    @PreAuthorize("hasRole('HOC_VIEN')")
//    public ResponseEntity<BaiNop> nopBai(@PathVariable Long maBaiKiemTra,
//                                         @RequestParam Long maNguoiDung,
//                                         @RequestParam String loaiBaiNop,
//                                         @RequestBody String noiDung) throws Exception {
//        return ResponseEntity.ok(baiNopService.nopBai(maNguoiDung, maBaiKiemTra, loaiBaiNop, noiDung));
//    }
//
//    @GetMapping("/bai-nop")
//    @PreAuthorize("hasRole('HOC_VIEN')")
//    public ResponseEntity<List<BaiNop>> layDanhSachBaiNop(@RequestParam Long maNguoiDung) {
//        return ResponseEntity.ok(baiNopService.layDanhSachBaiNop(maNguoiDung));
//    }
//}
