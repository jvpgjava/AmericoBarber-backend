package com.americobarber.mapper;

import com.americobarber.dto.response.AppointmentResponse;
import com.americobarber.entity.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AppointmentMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.name", target = "clientName")
    @Mapping(source = "barber.id", target = "barberId")
    @Mapping(source = "barber.name", target = "barberName")
    @Mapping(source = "service.id", target = "serviceId")
    @Mapping(source = "service.name", target = "serviceName")
    AppointmentResponse toResponse(Appointment appointment);
}
