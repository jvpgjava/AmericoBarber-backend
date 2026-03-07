package com.americobarber.mapper;

import com.americobarber.dto.response.AppointmentResponse;
import com.americobarber.entity.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {ServiceMapper.class})
public interface AppointmentMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.name", target = "clientName")
    @Mapping(source = "barber.id", target = "barberId")
    @Mapping(source = "barber.name", target = "barberName")
    @Mapping(source = "client.phone", target = "clientPhone")
    @Mapping(source = "barber.phone", target = "barberPhone")
    AppointmentResponse toResponse(Appointment appointment);
}
