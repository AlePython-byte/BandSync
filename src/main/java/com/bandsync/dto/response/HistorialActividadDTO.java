package com.bandsync.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialActividadDTO {

    private String bandaId;
    private List<ShowResponseDTO> shows;
    private List<EnsayoResponseDTO> ensayos;
    private List<ContratoResponseDTO> contratos;
    // keys esperadas: totalShows, ciudadesVisitadas, ensayosUltimoAnio
    private Map<String, Object> metricas;
}
