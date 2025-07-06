package com.example.dermAI.mapper.User;


import com.example.dermAI.dto.User.request.RegisterRequest;
import com.example.dermAI.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "USER")
    User toEntity(RegisterRequest dto);
}
