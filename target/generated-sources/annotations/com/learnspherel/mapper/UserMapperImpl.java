package com.learnspherel.mapper;

import com.learnspherel.dto.RegisterRequest;
import com.learnspherel.dto.UserResponse;
import com.learnspherel.entity.NguoiDung;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-22T20:04:51+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.15 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public NguoiDung toUser(RegisterRequest request) {
        if ( request == null ) {
            return null;
        }

        NguoiDung.NguoiDungBuilder nguoiDung = NguoiDung.builder();

        nguoiDung.tenNguoiDung( request.getTenNguoiDung() );
        nguoiDung.tenDangNhap( request.getTenDangNhap() );
        nguoiDung.matKhau( request.getMatKhau() );
        nguoiDung.email( request.getEmail() );

        return nguoiDung.build();
    }

    @Override
    public UserResponse toDto(NguoiDung user) {
        if ( user == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.maNguoiDung( user.getMaNguoiDung() );
        userResponse.tenNguoiDung( user.getTenNguoiDung() );
        userResponse.tenDangNhap( user.getTenDangNhap() );
        userResponse.email( user.getEmail() );
        userResponse.vaiTro( user.getVaiTro() );
        userResponse.anhDaiDien( user.getAnhDaiDien() );

        return userResponse.build();
    }
}
