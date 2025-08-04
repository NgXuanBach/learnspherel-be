//package com.learnspherel.controller;
//
//import com.learnspherel.entity.KhaoSat;
//import com.learnspherel.service.KhaoSatService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api")
//public class KhaoSatController {
//    @Autowired
//    private KhaoSatService khaoSatService;
//
//    @PostMapping("/khao-sat")
//    @PreAuthorize("hasRole('HOC_VIEN')")
//    public ResponseEntity<KhaoSat> guiKhaoSat(@RequestParam Long maNguoiDung, @RequestBody Map<String, Object> cauTraLoi) throws Exception {
//        return ResponseEntity.ok(khaoSatService.guiKhaoSat(maNguoiDung, cauTraLoi));
//    }
//
//    @GetMapping("/khao-sat")
//    @PreAuthorize("hasRole('HOC_VIEN')")
//    public ResponseEntity<KhaoSat> layKhaoSat(@RequestParam Long maNguoiDung) {
//        return ResponseEntity.ok(khaoSatService.layKhaoSat(maNguoiDung));
//    }
//}