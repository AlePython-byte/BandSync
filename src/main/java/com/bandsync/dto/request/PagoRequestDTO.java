package com.bandsync.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoRequestDTO {

    @NotBlank
    private String showId;

    @NotNull
    @DecimalMin("0.0")
    private Double montoBruto;

    @DecimalMin("0.0")
    private Double gastosProduccion;

    // key: artistaId, value: porcentaje
    private Map<String, Double> distribuciones;
}
