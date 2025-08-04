//package com.learnspherel.controller;
//
//import com.learnspherel.entity.KhoaHoc;
//import com.learnspherel.service.GoiYService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api")
//public class GoiYController {
//    @Autowired
//    private GoiYService goiYService;
//
//    @GetMapping("/goi-y")
//    @PreAuthorize("hasRole('HOC_VIEN')")
//    public ResponseEntity<List<KhoaHoc>> layGoiYKhoaHoc(@RequestParam Long maNguoiDung) {
//        return ResponseEntity.ok(goiYService.layGoiYKhoaHoc(maNguoiDung));
//    }
//}