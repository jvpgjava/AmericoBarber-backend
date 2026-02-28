package com.americobarber.mapper;

import com.americobarber.dto.response.UserResponse;
import com.americobarber.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @org.mapstruct.Mapping(source = "assignedBarber.id", target = "assignedBarberId")
    UserResponse toResponse(User user);
}
