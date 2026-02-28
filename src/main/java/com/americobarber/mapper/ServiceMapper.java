package com.americobarber.mapper;

import com.americobarber.dto.request.ServiceRequest;
import com.americobarber.dto.response.ServiceResponse;
import com.americobarber.entity.ServiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ServiceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "barber", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "active", defaultExpression = "java(true)")
    ServiceEntity toEntity(ServiceRequest request);

    @Mapping(source = "barber.id", target = "barberId")
    @Mapping(source = "barber.name", target = "barberName")
    ServiceResponse toResponse(ServiceEntity entity);
}
