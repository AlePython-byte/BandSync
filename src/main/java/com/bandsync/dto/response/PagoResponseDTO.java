package com.bandsync.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoResponseDTO {

    private String id;
    private String bandaId;
    private String showId;
    private Double montoBruto;
    private Double gastosProduccion;
    private Double montoNeto;
    private LocalDate fecha;
    private Map<String, Double> distribuciones;
}
