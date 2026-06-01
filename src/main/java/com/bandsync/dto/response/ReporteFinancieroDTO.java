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
public class ReporteFinancieroDTO {

    private String bandaId;
    private LocalDate periodoInicio;
    private LocalDate periodoFin;
    private Double ingresoTotalShows;
    private Double ingresoTotalPatrocinios;
    private Double gastoTotal;
    private Double utilidadNeta;
    private Map<String, Double> distribucionPorArtista;
    private int cantidadShows;
}
