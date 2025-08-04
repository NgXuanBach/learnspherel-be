package com.learnspherel.service;

import com.learnspherel.entity.NguoiDung;
import com.learnspherel.repository.NguoiDungRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final NguoiDungRepository userRepository;

    // Constructor injection
    public CustomUserDetailsService(NguoiDungRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String tenDangNhap) throws UsernameNotFoundException {
        // Retrieve the user from the database
        NguoiDung nguoiDung = userRepository.findByUsernameOrEmail(tenDangNhap)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + tenDangNhap));
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + nguoiDung.getVaiTro())
        );
        return new User(nguoiDung.getTenDangNhap(), nguoiDung.getMatKhau(), authorities);

    }
}
