package com.learnspherel.provider;//package com.learnsphere.learnsphere.provider;
//
//import com.example.Airbnb.entity.NguoiDungEntity;
//import com.example.Airbnb.repository.NguoiDungRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//
//@Service
//public class CustomAuthenProvider implements AuthenticationProvider {
//    @Autowired
//    private NguoiDungRepository nguoiDungRepository;
//
//    @Autowired
//    @Lazy // quét sau cùng để nó config bên securityconfig trước rồi mình lấy sau
//    private PasswordEncoder passwordEncoder;
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        String email = authentication.getName();
//        String password = authentication.getCredentials().toString();
//        NguoiDungEntity user = nguoiDungRepository.findByEmail(email);
//        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
//            return new UsernamePasswordAuthenticationToken(email, user.getPassword(), new ArrayList<>());
//        }
//        return null;
//    }
//
//    @Override
//    public boolean supports(Class<?> authentication) {
//        return authentication.equals(UsernamePasswordAuthenticationToken.class);
//    }
//}