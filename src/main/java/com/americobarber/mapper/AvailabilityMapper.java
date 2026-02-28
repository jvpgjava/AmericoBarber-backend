package com.americobarber.mapper;

import com.americobarber.dto.request.AvailabilityRequest;
import com.americobarber.dto.response.AvailabilityResponse;
import com.americobarber.entity.Availability;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AvailabilityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "barber", ignore = true)
    Availability toEntity(AvailabilityRequest request);

    @Mapping(source = "barber.id", target = "barberId")
    AvailabilityResponse toResponse(Availability availability);
}
