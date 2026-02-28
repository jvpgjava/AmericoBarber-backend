package com.americobarber.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Lista de datas em que o barbeiro não atende")
public class BarberDateOffRequest {

    @NotNull
    @Schema(description = "Datas em que não está disponível (ex.: folgas, feriados)", required = true)
    private List<LocalDate> datesOff;
}
