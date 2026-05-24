package com.americobarber.mapper;

import com.americobarber.dto.response.CancellationPenaltyResponse;
import com.americobarber.entity.CancellationPenalty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CancellationPenaltyMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.name", target = "clientName")
    @Mapping(source = "client.email", target = "clientEmail")
    @Mapping(source = "client.phone", target = "clientPhone")
    @Mapping(source = "appointment.id", target = "appointmentId")
    @Mapping(source = "appointment.date", target = "appointmentDate")
    @Mapping(source = "appointment.startTime", target = "appointmentStartTime")
    @Mapping(source = "reviewedBy.id", target = "reviewedById")
    @Mapping(source = "reviewedBy.name", target = "reviewedByName")
    CancellationPenaltyResponse toResponse(CancellationPenalty penalty);
}
