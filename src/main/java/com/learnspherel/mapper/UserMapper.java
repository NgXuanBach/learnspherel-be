package com.learnspherel.mapper;

import com.learnspherel.dto.RegisterRequest;
import com.learnspherel.dto.UserResponse;
import com.learnspherel.entity.NguoiDung;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    NguoiDung toUser(RegisterRequest request);
    UserResponse toDto(NguoiDung user);

}
